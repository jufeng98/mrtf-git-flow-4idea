package com.github.xiaolyuh.http.gutter

import com.dbn.utils.TooltipUtils.showTooltip
import com.github.xiaolyuh.http.HttpRequestEnum
import com.github.xiaolyuh.http.HttpRequestEnum.Companion.convertToReqBody
import com.github.xiaolyuh.http.HttpRequestEnum.Companion.convertToReqHeaderMap
import com.github.xiaolyuh.http.js.JsScriptExecutor
import com.github.xiaolyuh.http.psi.*
import com.github.xiaolyuh.http.resolve.VariableResolver
import com.github.xiaolyuh.http.ui.HttpExecutionConsoleToolWindow
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.application.runWriteActionAndWait
import com.intellij.openapi.editor.ex.EditorGutterComponentEx
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import java.awt.event.MouseEvent
import java.net.http.HttpClient.Version
import java.util.concurrent.CompletableFuture

class HttpGutterIconNavigationHandler(val element: HttpMethod) : GutterIconNavigationHandler<PsiElement> {
    override fun navigate(e: MouseEvent, elt: PsiElement) {
        val project = element.project
        val httpUrl = PsiTreeUtil.getNextSiblingOfType(element, HttpUrl::class.java)
        if (httpUrl == null) {
            showTooltip("url不正确!", project)
            return
        }

        val httpHeaders = PsiTreeUtil.getNextSiblingOfType(httpUrl, HttpHeaders::class.java)

        val httpBody = PsiTreeUtil.getNextSiblingOfType(httpUrl, HttpBody::class.java)

        val httpScript = PsiTreeUtil.getNextSiblingOfType(httpUrl, HttpScript::class.java)

        val httpRequestEnum = HttpRequestEnum.getInstance(element)
        val jsScriptExecutor = JsScriptExecutor.getService(project)
        val variableResolver = VariableResolver(jsScriptExecutor)

        val version = Version.HTTP_1_1

        val url: String
        val reqHeaderMap: MutableMap<String, String>
        val reqBody: Any?
        try {
            url = variableResolver.resolve(httpUrl.text!!)
            reqHeaderMap = convertToReqHeaderMap(httpHeaders, variableResolver)
            reqBody = convertToReqBody(httpBody, variableResolver)
        } catch (e: IllegalArgumentException) {
            showTooltip(e.message!!, project)
            return
        }

        val httpFile = element.containingFile
        val httpRequests =
            PsiTreeUtil.getChildrenOfType(httpFile, com.github.xiaolyuh.http.psi.HttpRequest::class.java)!!
        val beforeJsScripts = httpRequests
            .filter { it.script != null && it.script!!.prevSibling == null }
            .mapNotNull {
                getJsScript(it.script)
            }

        val beforeJsResList = jsScriptExecutor.evalJsBeforeRequest(beforeJsScripts)

        val httpReqDescList: MutableList<String> = mutableListOf()
        httpReqDescList.addAll(beforeJsResList)

        val jsScriptStr = getJsScript(httpScript)

        val component = e.component as EditorGutterComponentEx
        val loadingRemover = component.setLoadingIconForCurrentGutterMark()!!

        CompletableFuture.supplyAsync {
            httpRequestEnum.execute(
                url,
                version,
                reqHeaderMap,
                reqBody,
                jsScriptStr,
                jsScriptExecutor,
                httpReqDescList
            )
        }.whenComplete { httpInfo, throwable ->
            runInEdt {
                loadingRemover.run()

                val toolWindowManager = ToolWindowManager.getInstance(project)
                val toolWindow = toolWindowManager.getToolWindow("Http Execution")!!
                toolWindow.isAvailable = true

                toolWindow.activate {
                    runWriteActionAndWait {
                        val contentManager = toolWindow.contentManager

                        val parentDisposer = Disposer.newDisposable()

                        val form = HttpExecutionConsoleToolWindow()

                        form.initPanelData(httpInfo, throwable, project, parentDisposer)

                        val content = contentManager.factory.createContent(form.mainPanel, element.text, false)

                        content.setDisposer(parentDisposer)

                        contentManager.addContent(content)
                        contentManager.setSelectedContent(content)
                    }
                }
            }
        }

    }

    private fun getJsScript(httpScript: HttpScript?): String? {
        if (httpScript == null) {
            return null
        }

        val text = httpScript.text
        return text.substring(4, text.length - 2)
    }
}
