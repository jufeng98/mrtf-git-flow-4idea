package com.github.xiaolyuh.utils

import com.github.xiaolyuh.http.psi.HttpBody
import com.github.xiaolyuh.http.psi.HttpHeaders
import com.github.xiaolyuh.http.psi.HttpTypes
import com.github.xiaolyuh.http.resolve.VariableResolver
import com.google.gson.JsonElement
import com.intellij.psi.util.elementType
import org.apache.commons.io.FileUtils
import java.io.File
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import kotlin.jvm.optionals.getOrElse

object HttpUtils {
    fun convertToReqHeaderMap(
        httpHeaders: HttpHeaders?,
        variableResolver: VariableResolver,
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
                    map[split[0]] = variableResolver.resolve(split[1])
                }
        }
        return map
    }

    fun convertToReqBody(
        httpBody: HttpBody?,
        variableResolver: VariableResolver,
    ): Any? {
        if (httpBody == null) {
            return null
        }

        val ordinaryContent = httpBody.ordinaryContent
        if (ordinaryContent != null) {
            val elementType = ordinaryContent.firstChild.elementType
            if (elementType == HttpTypes.JSON_TEXT || elementType == HttpTypes.XML_TEXT) {
                return variableResolver.resolve(ordinaryContent.text)
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
                return variableResolver.resolve(str)
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
