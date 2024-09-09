package com.github.xiaolyuh.utils

import com.github.xiaolyuh.http.psi.*
import com.github.xiaolyuh.http.resolve.VariableResolver
import com.github.xiaolyuh.http.runconfig.HttpConfigurationType
import com.github.xiaolyuh.http.runconfig.HttpRunConfiguration
import com.google.gson.JsonElement
import com.intellij.execution.RunManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiComment
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.elementType
import org.apache.commons.io.FileUtils
import java.io.File
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import kotlin.jvm.optionals.getOrElse

object HttpUtils {
    fun saveConfiguration(tabName: String?, project: Project, selectedEnv: String?, httpMethod: HttpMethod) {
        val runName: String = tabName ?: "#http-client"
        val runManager = RunManager.getInstance(project)

        var config = runManager.findConfigurationByName(runName)
        if (config == null) {
            config = runManager.createConfiguration(runName, HttpConfigurationType::class.java)
            val httpRunConfiguration = config.configuration as HttpRunConfiguration
            httpRunConfiguration.env = selectedEnv ?: ""
            httpRunConfiguration.httpFilePath = httpMethod.containingFile.virtualFile.path
            runManager.addConfiguration(config)
            runManager.selectedConfiguration = config
        } else {
            val httpRunConfiguration = config.configuration as HttpRunConfiguration
            httpRunConfiguration.env = selectedEnv ?: ""
            httpRunConfiguration.httpFilePath = httpMethod.containingFile.virtualFile.path
            runManager.selectedConfiguration = config
        }
    }

    fun getTabName(httpMethod: HttpMethod): String? {
        val httpRequest =
            PsiTreeUtil.getParentOfType(httpMethod, com.github.xiaolyuh.http.psi.HttpRequest::class.java)!!

        val comment = PsiTreeUtil.findChildOfType(httpRequest, PsiComment::class.java)
        if (comment != null || httpRequest.prevSibling is HttpRequest) {
            return null
        }

        val psiComment = PsiTreeUtil.getPrevSiblingOfType(httpRequest, PsiComment::class.java) ?: return null
        return httpMethod.text + " " + psiComment.text.replace("#", "").trim()
    }

    fun convertToReqHeaderMap(
        httpHeaders: HttpHeaders?,
        variableResolver: VariableResolver,
        selectedEnv: String?,
    ): MutableMap<String, String> {
        val map = mutableMapOf<String, String>()
        if (httpHeaders != null) {
            val headerList = httpHeaders.headerList
            headerList.stream()
                .forEach {
                    val text = it.text
                    val split = text.split(":")
                    if (split.size != 2) {
                        throw IllegalArgumentException("请求头${text}有错误")
                    }
                    map[split[0]] = variableResolver.resolve(split[1], selectedEnv)
                }
        }
        return map
    }

    fun convertToReqBody(
        httpBody: HttpBody?,
        variableResolver: VariableResolver,
        selectedEnv: String?,
    ): Any? {
        if (httpBody == null) {
            return null
        }

        val ordinaryContent = httpBody.ordinaryContent
        if (ordinaryContent != null) {
            val elementType = ordinaryContent.firstChild.elementType
            if (elementType == HttpTypes.JSON_TEXT || elementType == HttpTypes.XML_TEXT) {
                return variableResolver.resolve(ordinaryContent.text, selectedEnv)
            }

            val httpFile = ordinaryContent.file
            if (httpFile != null) {
                val path = httpBody.containingFile.virtualFile.path
                val filePath = httpFile.filePath.text
                val file = File(File(path).parentFile, filePath)
                if (!file.exists()) {
                    throw IllegalArgumentException("文件${file}不存在")
                }

                val str = FileUtils.readFileToString(file, StandardCharsets.UTF_8)
                return variableResolver.resolve(str, selectedEnv)
            }
        }

        return null
    }

    fun convertToResHeaderDescList(
        response: HttpResponse<ByteArray>,
    ): MutableList<String> {
        val headerDescList = mutableListOf<String>()
        val headers = response.headers()
        headers.map()
            .forEach { (t, u) ->
                u.forEach {
                    headerDescList.add("$t: $it\r\n")
                }
            }
        headerDescList.add("\r\n")
        return headerDescList
    }

    fun convertToResPair(response: HttpResponse<ByteArray>): Pair<String, ByteArray> {
        val resBody = response.body()
        val resHeaders = response.headers()
        val contentType = resHeaders.firstValue(org.apache.http.HttpHeaders.CONTENT_TYPE).getOrElse { "text/plain" }

        if (contentType.contains("json")) {
            val jsonStr = String(resBody, StandardCharsets.UTF_8)
            val jsonElement = HttpClientUtil.gson.fromJson(jsonStr, JsonElement::class.java)
            val jsonStrPretty = HttpClientUtil.gson.toJson(jsonElement)
            return Pair("json", jsonStrPretty.toByteArray(StandardCharsets.UTF_8))
        }

        if (contentType.contains("html")) {
            return Pair("html", resBody)
        }

        if (contentType.contains("xml")) {
            return Pair("xml", resBody)
        }

        if (contentType.contains("jpg") || contentType.contains("jpeg") || contentType.contains("png")) {
            return Pair("image", resBody)
        }

        return Pair("txt", resBody)
    }
}
