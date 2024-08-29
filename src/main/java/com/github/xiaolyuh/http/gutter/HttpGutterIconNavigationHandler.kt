package com.github.xiaolyuh.http.gutter

import com.dbn.utils.TooltipUtils.showTooltip
import com.github.xiaolyuh.http.HttpRequestEnum
import com.github.xiaolyuh.http.js.JsScriptExecutor
import com.github.xiaolyuh.http.psi.*
import com.github.xiaolyuh.http.resolve.VariableResolver
import com.github.xiaolyuh.http.runconfig.HttpConfigurationType
import com.github.xiaolyuh.http.runconfig.HttpRunConfiguration
import com.github.xiaolyuh.http.ui.HttpExecutionConsoleToolWindow
import com.github.xiaolyuh.http.ws.WsRequest
import com.github.xiaolyuh.utils.HttpUtils.convertToReqBody
import com.github.xiaolyuh.utils.HttpUtils.convertToReqHeaderMap
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler
import com.intellij.execution.RunManager
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.application.runWriteActionAndWait
import com.intellij.openapi.editor.ex.EditorGutterComponentEx
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.MessageType
import com.intellij.openapi.util.Disposer.newDisposable
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.ui.content.Content
import java.awt.event.MouseEvent
import java.net.http.HttpClient.Version
import java.util.concurrent.CompletableFuture

class HttpGutterIconNavigationHandler(private val httpMethod: HttpMethod) : GutterIconNavigationHandler<PsiElement> {
    override fun navigate(e: MouseEvent, elt: PsiElement) {
        val project = httpMethod.project
        val tabName = getTabName(httpMethod)

        val runName: String = tabName ?: "#http-client"
        val runManager = RunManager.getInstance(project)

        var config = runManager.findConfigurationByName(runName)
        if (config == null) {
            config = runManager.createConfiguration(runName, HttpConfigurationType::class.java)
            val httpRunConfiguration = config.configuration as HttpRunConfiguration
            runManager.addConfiguration(config)
            runManager.selectedConfiguration = config
        } else {
            runManager.selectedConfiguration = config
        }


        val httpUrl = PsiTreeUtil.getNextSiblingOfType(httpMethod, HttpUrl::class.java)
        if (httpUrl == null) {
            showTooltip("url不正确!", project)
            return
        }

        val httpHeaders = PsiTreeUtil.getNextSiblingOfType(httpUrl, HttpHeaders::class.java)

        val httpBody = PsiTreeUtil.getNextSiblingOfType(httpUrl, HttpBody::class.java)

        val httpScript = PsiTreeUtil.getNextSiblingOfType(httpUrl, HttpScript::class.java)

        val httpRequestEnum = HttpRequestEnum.getInstance(httpMethod)
        val jsScriptExecutor = JsScriptExecutor.getService(project)
        val variableResolver = VariableResolver.getService(project)

        jsScriptExecutor.prepareJsRequestObj()

        val httpFile = httpMethod.containingFile
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

        val version = Version.HTTP_1_1
        val url: String
        val reqHeaderMap: MutableMap<String, String>
        val reqBody: Any?
        try {
            url = variableResolver.resolve(httpUrl.text!!)
            reqHeaderMap = convertToReqHeaderMap(httpHeaders, variableResolver)
            reqBody = convertToReqBody(httpBody, variableResolver)
        } catch (e: IllegalArgumentException) {
            val toolWindowManager = ToolWindowManager.getInstance(project)
            toolWindowManager.notifyByBalloon(TOOL_WINDOW_ID, MessageType.WARNING, "<div>${e.message}</div>")
            return
        }

        if (url.startsWith("ws")) {
            val wsRequest = WsRequest(url, reqHeaderMap, project)
            wsRequest.handleWsConnect()
            return
        }

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

                jsScriptExecutor.clearJsRequestObj()

                runWriteActionAndWait {
                    val parentDisposer = newDisposable()

                    val form = HttpExecutionConsoleToolWindow()

                    form.initPanelData(httpInfo, throwable, tabName, project, parentDisposer)

                    initAndShowTabContent(tabName, form, parentDisposer, project)
                }
            }
        }

    }

    private fun initAndShowTabContent(
        tabName: String?,
        form: HttpExecutionConsoleToolWindow,
        parentDisposer: Disposable,
        project: Project,
    ) {
        val toolWindowManager = ToolWindowManager.getInstance(project)
        val toolWindow = toolWindowManager.getToolWindow(TOOL_WINDOW_ID)!!
        val contentManager = toolWindow.contentManager

        var content: Content?
        if (tabName == null) {
            content = contentManager.factory.createContent(form.mainPanel, httpMethod.text, false)
            content.setDisposer(parentDisposer)
        } else {
            content = contentManager.findContent(tabName)
            if (content != null) {
                content.component = form.mainPanel
            } else {
                content = contentManager.factory.createContent(form.mainPanel, tabName, false)
                content.setDisposer(parentDisposer)
            }
        }

        contentManager.addContent(content)
        contentManager.setSelectedContent(content)

        toolWindow.activate {}
    }

    private fun getTabName(httpMethod: HttpMethod): String? {
        val httpRequest =
            PsiTreeUtil.getParentOfType(httpMethod, com.github.xiaolyuh.http.psi.HttpRequest::class.java)!!

        val comment = PsiTreeUtil.findChildOfType(httpRequest, PsiComment::class.java)
        if (comment != null || httpRequest.prevSibling is HttpRequest) {
            return null
        }

        val psiComment = PsiTreeUtil.getPrevSiblingOfType(httpRequest, PsiComment::class.java) ?: return null
        return httpMethod.text + " " + psiComment.text.replace("#", "").trim()
    }

    private fun getJsScript(httpScript: HttpScript?): String? {
        if (httpScript == null) {
            return null
        }

        val text = httpScript.text
        return text.substring(4, text.length - 2)
    }

    companion object {
        const val TOOL_WINDOW_ID: String = "Http Execution"
    }
}
