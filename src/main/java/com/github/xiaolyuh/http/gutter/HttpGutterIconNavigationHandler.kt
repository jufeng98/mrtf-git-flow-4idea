package com.github.xiaolyuh.http.gutter

import com.dbn.utils.TooltipUtils.showTooltip
import com.github.xiaolyuh.http.HttpInfo
import com.github.xiaolyuh.http.HttpRequestEnum
import com.github.xiaolyuh.http.js.JsScriptExecutor
import com.github.xiaolyuh.http.psi.*
import com.github.xiaolyuh.http.resolve.VariableResolver
import com.github.xiaolyuh.http.ui.HttpEditorTopForm
import com.github.xiaolyuh.http.ui.HttpExecutionConsoleToolWindow
import com.github.xiaolyuh.http.ws.WsRequest
import com.github.xiaolyuh.utils.HttpUtils
import com.github.xiaolyuh.utils.HttpUtils.convertToReqBody
import com.github.xiaolyuh.utils.HttpUtils.convertToReqHeaderMap
import com.github.xiaolyuh.utils.HttpUtils.getJsScript
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.application.runWriteActionAndWait
import com.intellij.openapi.editor.ex.EditorGutterComponentEx
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.MessageType
import com.intellij.openapi.util.Disposer.newDisposable
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import java.awt.event.MouseEvent
import java.io.ByteArrayInputStream
import java.io.File
import java.net.http.HttpClient.Version
import java.nio.file.Files
import java.util.concurrent.CompletableFuture

class HttpGutterIconNavigationHandler(private val httpMethod: HttpMethod) : GutterIconNavigationHandler<PsiElement> {
    override fun navigate(mouseEvent: MouseEvent, elt: PsiElement) {
        val component = mouseEvent.component as EditorGutterComponentEx
        val selectedEnv = HttpEditorTopForm.getSelectedEnv(httpMethod.project)

        doRequest(component, selectedEnv)
    }

    fun doRequest(component: EditorGutterComponentEx?, selectedEnv: String?) {
        val project = httpMethod.project
        val tabName = HttpUtils.getTabName(httpMethod)

        HttpUtils.saveConfiguration(tabName, project, selectedEnv, httpMethod)

        val httpUrl = PsiTreeUtil.getNextSiblingOfType(httpMethod, HttpUrl::class.java)
        if (httpUrl == null) {
            showTooltip("url不正确!", project)
            return
        }

        val httpHeaders = PsiTreeUtil.getNextSiblingOfType(httpUrl, HttpHeaders::class.java)

        val httpBody = PsiTreeUtil.getNextSiblingOfType(httpUrl, HttpBody::class.java)

        val httpScript = PsiTreeUtil.getNextSiblingOfType(httpUrl, HttpScript::class.java)

        val httpOutputFile = PsiTreeUtil.getNextSiblingOfType(httpUrl, HttpOutputFile::class.java)

        val httpRequestEnum = HttpRequestEnum.getInstance(httpMethod)
        val jsScriptExecutor = JsScriptExecutor.getService(project)
        val variableResolver = VariableResolver.getService(project)

        jsScriptExecutor.prepareJsRequestObj()

        val httpFile = httpMethod.containingFile
        val beforeJsScripts = HttpUtils.collectBeforeJsScripts(httpFile)

        val beforeJsResList = jsScriptExecutor.evalJsBeforeRequest(beforeJsScripts)

        val httpReqDescList: MutableList<String> = mutableListOf()
        httpReqDescList.addAll(beforeJsResList)

        val version = Version.HTTP_1_1
        val url: String
        val reqHeaderMap: MutableMap<String, String>
        val reqBody: Any?
        try {
            val definitions = PsiTreeUtil.findChildrenOfType(httpFile, HttpDefinition::class.java)
            variableResolver.addFileScopeVariables(definitions, selectedEnv)

            url = variableResolver.resolve(httpUrl.text!!, selectedEnv)

            reqHeaderMap = convertToReqHeaderMap(httpHeaders, variableResolver, selectedEnv)
            val match = reqHeaderMap.keys.stream().anyMatch { it.equals("Content-Length") }
            if (match) {
                throw IllegalArgumentException("不能有 Content-Length 请求头!")
            }

            reqBody = convertToReqBody(httpBody, variableResolver, selectedEnv)
        } catch (e: IllegalArgumentException) {
            val toolWindowManager = ToolWindowManager.getInstance(project)
            toolWindowManager.notifyByBalloon(TOOL_WINDOW_ID, MessageType.ERROR, "<div>${e.message}</div>")
            return
        }

        if (url.startsWith("ws")) {
            val parentDisposer = newDisposable()
            val wsRequest = WsRequest(url, reqHeaderMap, project, parentDisposer)
            val form = HttpExecutionConsoleToolWindow()

            form.initPanelWsData(wsRequest)

            wsRequest.connect()

            initAndShowTabContent(tabName, form, parentDisposer, project)

            variableResolver.clearFileScopeVariables()

            return
        }

        variableResolver.clearFileScopeVariables()

        val jsScriptStr = getJsScript(httpScript)
        val parentPath = httpFile.virtualFile.parent.path
        var outPutFileName: String? = null
        if (httpOutputFile != null) {
            outPutFileName = httpOutputFile.filePath.text
        }

        val loadingRemover = component?.setLoadingIconForCurrentGutterMark()
        if (loadingRemover == null) {
            ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Sending request...", true) {
                override fun run(indicator: ProgressIndicator) {
                    val httpInfo = httpRequestEnum.execute(
                        url,
                        version,
                        reqHeaderMap,
                        reqBody,
                        jsScriptStr,
                        jsScriptExecutor,
                        httpReqDescList,
                        tabName
                    )
                    requestCompleted(project, httpInfo, null, tabName, outPutFileName, parentPath)
                }
            })
            return
        }

        CompletableFuture.supplyAsync {
            httpRequestEnum.execute(
                url,
                version,
                reqHeaderMap,
                reqBody,
                jsScriptStr,
                jsScriptExecutor,
                httpReqDescList,
                tabName
            )
        }.whenComplete { httpInfo, throwable ->
            runInEdt {
                loadingRemover.run()

                requestCompleted(project, httpInfo, throwable, tabName, outPutFileName, parentPath)
            }
        }
    }

    private fun requestCompleted(
        project: Project,
        httpInfo: HttpInfo,
        throwable: Throwable?,
        tabName: String,
        outPutFileName: String?,
        parentPath: String,
    ) {
        val jsScriptExecutor = JsScriptExecutor.getService(project)
        jsScriptExecutor.clearJsRequestObj()

        runWriteActionAndWait {
            val parentDisposer = newDisposable()

            val saveResult = saveResToFile(outPutFileName, parentPath, httpInfo.byteArray)
            if (!saveResult.isNullOrEmpty()) {
                httpInfo.httpResDescList.add(0, saveResult)
            }

            val form = HttpExecutionConsoleToolWindow()

            form.initPanelData(httpInfo, throwable, tabName, project, parentDisposer)

            initAndShowTabContent(tabName, form, parentDisposer, project)
        }
    }

    private fun saveResToFile(outPutFileName: String?, parentPath: String, byteArray: ByteArray?): String? {
        if (outPutFileName == null) {
            return null
        }

        if (byteArray == null) {
            return null
        }

        val path = HttpUtils.constructFilePath(outPutFileName, parentPath)
        val file = File(path)
        if (!file.parentFile.exists()) {
            Files.createDirectories(file.toPath())
        }

        if (file.exists()) {
            try {
                Files.delete(file.toPath())
            } catch (e: Exception) {
                return "# 保存失败, ${e.message?.trim()}\r\n"
            }
        }

        val inputStream = ByteArrayInputStream(byteArray)
        Files.copy(inputStream, file.toPath())
        return "# 响应已保存到 ${file.absolutePath}\r\n"
    }

    private fun initAndShowTabContent(
        tabName: String,
        form: HttpExecutionConsoleToolWindow,
        parentDisposer: Disposable,
        project: Project,
    ) {
        val toolWindowManager = ToolWindowManager.getInstance(project)
        val toolWindow = toolWindowManager.getToolWindow(TOOL_WINDOW_ID)!!
        val contentManager = toolWindow.contentManager

        var content = contentManager.findContent(tabName)
        if (content != null) {
            content.component = form.mainPanel
        } else {
            content = contentManager.factory.createContent(form.mainPanel, tabName, false)
            content.setDisposer(parentDisposer)
        }

        contentManager.addContent(content)
        contentManager.setSelectedContent(content)
        toolWindow.isAvailable = true

        toolWindowManager.notifyByBalloon(
            TOOL_WINDOW_ID,
            MessageType.INFO,
            "<div style='font-size:18pt''>Tip:请求已完成!</div>"
        )
    }

    companion object {
        const val TOOL_WINDOW_ID: String = "Http Execution"
    }
}
