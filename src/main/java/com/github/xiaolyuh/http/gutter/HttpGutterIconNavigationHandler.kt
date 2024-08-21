package com.github.xiaolyuh.http.gutter

import com.dbn.utils.TooltipUtils.showTooltip
import com.github.xiaolyuh.http.HttpRequestEnum
import com.github.xiaolyuh.http.HttpRequestEnum.Companion.convertToReqBodyPublisher
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
import java.net.http.HttpRequest
import java.util.concurrent.CompletableFuture

class HttpGutterIconNavigationHandler(val element: HttpMethod) : GutterIconNavigationHandler<PsiElement> {
    override fun navigate(e: MouseEvent, elt: PsiElement) {
        val httpUrl = PsiTreeUtil.getNextSiblingOfType(element, HttpUrl::class.java)
        if (httpUrl == null) {
            showTooltip("url不正确!", element.project)
            return
        }

        val httpHeaders = PsiTreeUtil.getNextSiblingOfType(httpUrl, HttpHeaders::class.java)

        val httpBody = PsiTreeUtil.getNextSiblingOfType(httpUrl, HttpBody::class.java)

        val httpScript = PsiTreeUtil.getNextSiblingOfType(httpUrl, HttpScript::class.java)

        val httpRequestEnum = HttpRequestEnum.getInstance(element)

        val variableResolver = VariableResolver()

        val url = variableResolver.resolve(httpUrl.text!!)
        val version = Version.HTTP_1_1
        val reqHeaderMap = convertToReqHeaderMap(httpHeaders, variableResolver)
        val bodyPublisher: HttpRequest.BodyPublisher?
        try {
            bodyPublisher = convertToReqBodyPublisher(httpBody, variableResolver)
        } catch (e: IllegalArgumentException) {
            showTooltip(e.message!!, element.project)
            return
        }
        var jsScriptStr: String? = null
        if (httpScript != null) {
            val text = httpScript.text
            jsScriptStr = text.substring(4, text.length - 2)
        }
        val jsScriptExecutor = JsScriptExecutor.getService(element.project)

        val component = e.component as EditorGutterComponentEx
        val loadingRemover = component.setLoadingIconForCurrentGutterMark()!!

        CompletableFuture.supplyAsync {
            httpRequestEnum.execute(
                url,
                version,
                reqHeaderMap,
                bodyPublisher,
                variableResolver.variableMap,
                jsScriptStr,
                jsScriptExecutor
            )
        }.whenComplete { resPair, throwable ->
            runInEdt {
                loadingRemover.run()
                if (throwable is IllegalArgumentException) {
                    showTooltip(throwable.message!!, element.project)
                    return@runInEdt
                }

                val toolWindowManager = ToolWindowManager.getInstance(element.project)
                val toolWindow = toolWindowManager.getToolWindow("Http Execution")!!
                toolWindow.isAvailable = true

                toolWindow.activate {
                    runWriteActionAndWait {
                        val contentManager = toolWindow.contentManager

                        val form = HttpExecutionConsoleToolWindow()
                        val textEditor = form.initPanelData(resPair, throwable, element.project)
                        val content = contentManager.factory.createContent(form.mainPanel, element.text, false)

                        content.setDisposer(Disposer.newDisposable())
                        val parentDisposer = content.disposer!!
                        if (textEditor != null) {
                            Disposer.register(parentDisposer, textEditor)
                        }

                        contentManager.addContent(content)
                        contentManager.setSelectedContent(content)
                    }
                }
            }
        }

    }
}
