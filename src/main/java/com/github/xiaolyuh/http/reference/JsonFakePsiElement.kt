package com.github.xiaolyuh.http.reference

import com.cool.request.view.tool.search.ApiAbstractGotoSEContributor
import com.github.xiaolyuh.http.reference.HttpFakePsiElement.Companion.createProcessIndicator
import com.github.xiaolyuh.http.reference.HttpFakePsiElement.Companion.findControllerPsiMethods
import com.github.xiaolyuh.http.reference.HttpFakePsiElement.Companion.getControllerNavigationItem
import com.github.xiaolyuh.http.reference.HttpFakePsiElement.Companion.showTip
import com.github.xiaolyuh.utils.HttpUtils
import com.github.xiaolyuh.utils.SpelUtils
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.json.psi.JsonProperty
import com.intellij.json.psi.JsonStringLiteral
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.util.Disposer
import com.intellij.psi.*
import com.intellij.psi.impl.source.PsiClassReferenceType
import com.intellij.psi.util.PsiTreeUtil
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * @author yudong
 */
class JsonFakePsiElement(
    private val jsonString: JsonStringLiteral,
    private val searchTxt: String,
    private val originalModule: Module?,
) :
    ASTWrapperPsiElement(jsonString.node) {

    override fun getPresentation(): ItemPresentation {
        return JsonItemPresentation
    }

    override fun navigate(requestFocus: Boolean) {
        val virtualFile = jsonString.containingFile.virtualFile
        val module = if (HttpUtils.isFileInIdeaDir(virtualFile)) {
            originalModule
        } else {
            ModuleUtil.findModuleForPsiElement(jsonString)
        }

        if (module == null) {
            return
        }

        val jsonPropertyNameLevels = collectJsonPropertyNameLevels(jsonString)
        if (jsonPropertyNameLevels.isEmpty()) {
            return
        }

        val event = HttpFakePsiElement.createEvent()
        val seContributor = ApiAbstractGotoSEContributor(event)

        val processIndicator = createProcessIndicator("Tip:正在尝试跳转到对应的Bean字段...", project)
        Disposer.register(Disposer.newDisposable(), processIndicator)

        CompletableFuture.runAsync {
            val list = seContributor.search(searchTxt, processIndicator)
            if (processIndicator.isCanceled) {
                processIndicator.processFinish()
                return@runAsync
            }

            processIndicator.processFinish()

            runInEdt {
                if (list.isEmpty()) {
                    showTip("Tip:未能解析到对应的controller mapping,无法跳转", project)
                    return@runInEdt
                }

                val controllerNavigationItem = getControllerNavigationItem(list, searchTxt)

                runReadAction {
                    val psiMethods = findControllerPsiMethods(controllerNavigationItem, module)
                    if (psiMethods.isEmpty()) {
                        showTip("Tip:未能解析对应的controller方法,无法跳转", project)
                        return@runReadAction
                    }

                    if (psiMethods.size > 1) {
                        showTip("Tip:解析到${psiMethods.size}个的controller方法,无法跳转", project)
                        return@runReadAction
                    }

                    val psiMethod = psiMethods[0]
                    val paramPsiType: PsiType?

                    if (virtualFile?.name?.endsWith("res.http") == true) {
                        paramPsiType = psiMethod.returnType
                    } else {
                        val psiParameters = psiMethod.parameterList.parameters
                        val psiParameter = psiParameters.firstOrNull {
                            it.hasAnnotation("org.springframework.web.bind.annotation.RequestBody")
                        }
                        paramPsiType = psiParameter?.type
                    }

                    val paramPsiCls: PsiClass = SpelUtils.resolvePsiType(paramPsiType) ?: return@runReadAction

                    val classGenericParameters = (paramPsiType as PsiClassReferenceType).parameters

                    val targetField = resolveTargetField(paramPsiCls, jsonPropertyNameLevels, classGenericParameters)
                    if (targetField == null) {
                        showTip("Tip:未能解析对应的Bean属性,无法跳转", project)
                        return@runReadAction
                    }

                    targetField.navigate(true)
                }
            }
        }
    }

    private fun resolveTargetField(
        paramPsiCls: PsiClass,
        jsonPropertyNameLevels: LinkedList<String>,
        classGenericParameters: Array<PsiType>,
    ): PsiField? {
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
                    if (psiFieldTypeCls is PsiTypeParameter) {
                        // 参数本身是泛型类型,如 T, 直接取第一个
                        val genericActualType = classGenericParameters[0] as PsiClassType
                        if (genericActualType.parameters.isNotEmpty()) {
                            val psiFieldGenericTypeCls =
                                SpelUtils.resolvePsiType(genericActualType.parameters[0]) ?: return null
                            fieldTypeCls = psiFieldGenericTypeCls
                        } else {
                            fieldTypeCls = SpelUtils.resolvePsiType(genericActualType) ?: return null
                        }
                    } else {
                        fieldTypeCls = psiFieldTypeCls
                    }
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
}
