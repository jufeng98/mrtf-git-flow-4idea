package com.github.xiaolyuh.http.reference

import com.cool.request.view.tool.search.ApiAbstractGotoSEContributor
import com.cool.request.view.tool.search.ControllerNavigationItem
import com.github.xiaolyuh.utils.SpelUtils
import com.intellij.codeInsight.hint.HintManager
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.json.psi.JsonProperty
import com.intellij.json.psi.JsonStringLiteral
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.progress.TaskInfo
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator
import com.intellij.openapi.util.Disposer
import com.intellij.psi.*
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * @author yudong
 */
class JsonFakePsiElement(private val jsonString: JsonStringLiteral, private val searchTxt: String) :
    ASTWrapperPsiElement(jsonString.node) {

    override fun getPresentation(): ItemPresentation {
        return JsonItemPresentation
    }

    override fun navigate(requestFocus: Boolean) {
        val module = ModuleUtil.findModuleForPsiElement(jsonString) ?: return

        val jsonPropertyNameLevels = collectJsonPropertyNameLevels(jsonString)
        if (jsonPropertyNameLevels.isEmpty()) {
            return
        }

        val event = HttpFakePsiElement.createEvent()
        val seContributor = ApiAbstractGotoSEContributor(event)

        val processIndicator = createProcessIndicator()
        Disposer.register(Disposer.newDisposable(), processIndicator)

        CompletableFuture.runAsync {
            val list = seContributor.search(searchTxt, processIndicator)
            if (processIndicator.isCanceled) {
                processIndicator.processFinish()
                return@runAsync
            }

            processIndicator.processFinish()

            runInEdt {
                val urlMap = list.groupBy {
                    val navigationItem = it as ControllerNavigationItem
                    navigationItem.url
                }
                val itemList = urlMap[searchTxt]

                if (itemList.isNullOrEmpty()) {
                    showTip("Tip:未能解析到对应的controller mapping,无法跳转")
                    return@runInEdt
                }

                runReadAction {
                    val controllerNavigationItem = itemList[0] as ControllerNavigationItem
                    val psiMethods = findControllerPsiMethods(controllerNavigationItem, module)
                    if (psiMethods.isEmpty()) {
                        showTip("Tip:未能解析对应的controller方法,无法跳转")
                        return@runReadAction
                    }

                    if (psiMethods.size > 1) {
                        showTip("Tip:解析到${psiMethods.size}个的controller方法,无法跳转")
                        return@runReadAction
                    }

                    val psiParameters = psiMethods[0].parameterList.parameters
                    val psiParameter = psiParameters.firstOrNull {
                        it.hasAnnotation("org.springframework.web.bind.annotation.RequestBody")
                    } ?: return@runReadAction

                    val paramPsiCls = SpelUtils.resolvePsiType(psiParameter.type) ?: return@runReadAction

                    val targetField = resolveTargetField(paramPsiCls, jsonPropertyNameLevels)
                    if (targetField == null) {
                        showTip("Tip:未能解析对应的Bean属性,无法跳转")
                        return@runReadAction
                    }

                    targetField.navigate(true)
                }
            }
        }
    }

    private fun createProcessIndicator(): BackgroundableProcessIndicator {
        return BackgroundableProcessIndicator(project, object : TaskInfo {
            override fun getTitle(): String {
                return "Tip:正在尝试跳转到对应的Bean字段..."
            }

            override fun getCancelText(): String {
                return "Tip:正在取消......"
            }

            override fun getCancelTooltipText(): String {
                return "Tip:取消中......"
            }

            override fun isCancellable(): Boolean {
                return true
            }
        })
    }

    private fun resolveTargetField(paramPsiCls: PsiClass, jsonPropertyNameLevels: LinkedList<String>): PsiField? {
        var fieldTypeCls = paramPsiCls
        var psiField: PsiField? = null
        var propertyName = jsonPropertyNameLevels.pop()

        try {
            while (true) {
                psiField = fieldTypeCls.findFieldByName(propertyName, true) ?: return null
                val psiType = psiField.type
                if (psiType !is PsiClassType) {
                    return null
                }

                val parameters = psiType.parameters
                if (parameters.isNotEmpty()) {
                    // 取得泛型参数类型
                    val psiFieldGenericTypeCls = SpelUtils.resolvePsiType(parameters[0]) ?: return null
                    fieldTypeCls = psiFieldGenericTypeCls
                } else {
                    val psiFieldTypeCls = SpelUtils.resolvePsiType(psiType) ?: return null
                    fieldTypeCls = psiFieldTypeCls
                }

                propertyName = jsonPropertyNameLevels.pop()
            }
        } catch (_: NoSuchElementException) {
        }

        return psiField
    }

    private fun collectJsonPropertyNameLevels(jsonString: JsonStringLiteral): LinkedList<String> {
        val beanFieldLevels = LinkedList<String>()

        var jsonProperty = PsiTreeUtil.getParentOfType(jsonString, JsonProperty::class.java)
        while (jsonProperty != null) {
            val propertyName = getJsonPropertyName(jsonProperty)
            beanFieldLevels.push(propertyName)
            jsonProperty = PsiTreeUtil.getParentOfType(jsonProperty, JsonProperty::class.java)
        }

        return beanFieldLevels
    }

    private fun getJsonPropertyName(jsonProperty: JsonProperty): String {
        val nameElement = jsonProperty.nameElement
        val name = nameElement.text
        return name.substring(1, name.length - 1)
    }

    private fun findControllerPsiMethods(
        navigationItem: ControllerNavigationItem,
        module: Module,
    ): Array<out PsiMethod> {
        val controllerFullClassName = navigationItem.javaClassName
        val controllerMethodName = navigationItem.methodName

        val controllerPsiCls = JavaPsiFacade.getInstance(project)
            .findClass(
                controllerFullClassName,
                GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module)
            ) ?: return arrayOf()

        val psiMethods = controllerPsiCls.findMethodsByName(controllerMethodName, false)
        if (psiMethods.isEmpty()) {
            return arrayOf()
        }

        return psiMethods
    }

    private fun showTip(msg: String) {
        val textEditor = FileEditorManager.getInstance(project).selectedTextEditor ?: return
        HintManager.getInstance().showInformationHint(textEditor, msg)
    }
}
