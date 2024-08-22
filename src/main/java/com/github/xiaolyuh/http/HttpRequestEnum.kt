package com.github.xiaolyuh.http

import com.github.xiaolyuh.http.js.JsScriptExecutor
import com.github.xiaolyuh.http.psi.HttpBody
import com.github.xiaolyuh.http.psi.HttpHeaders
import com.github.xiaolyuh.http.psi.HttpMethod
import com.github.xiaolyuh.http.psi.HttpTypes
import com.github.xiaolyuh.http.resolve.VariableResolver
import com.github.xiaolyuh.utils.HttpClientUtil
import com.google.gson.JsonElement
import com.intellij.psi.util.PsiUtilCore
import com.intellij.psi.util.elementType
import org.apache.commons.io.FileUtils
import java.io.File
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpClient.Version
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import java.time.Duration
import kotlin.jvm.optionals.getOrElse

enum class HttpRequestEnum {
    GET {
        override fun createRequest(
            url: String,
            version: Version,
            reqHeaderMap: MutableMap<String, String>,
            bodyPublisher: HttpRequest.BodyPublisher?,
        ): HttpRequest {
            val builder = HttpRequest.newBuilder()
                .version(Version.HTTP_1_1)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .uri(URI.create(url))

            reqHeaderMap.forEach(builder::setHeader)

            return builder.build()
        }
    },
    POST {
        override fun createRequest(
            url: String,
            version: Version,
            reqHeaderMap: MutableMap<String, String>,
            bodyPublisher: HttpRequest.BodyPublisher?,
        ): HttpRequest {
            val builder = HttpRequest.newBuilder()
                .version(Version.HTTP_1_1)
                .timeout(Duration.ofSeconds(30))
                .uri(URI.create(url))

            reqHeaderMap.forEach(builder::setHeader)

            if (bodyPublisher != null) {
                builder.POST(bodyPublisher)
            }

            return builder.build()
        }
    },
    ;

    fun execute(
        url: String,
        version: Version,
        reqHttpHeaders: MutableMap<String, String>,
        reqBody: Any?,
        jsScriptStr: String?,
        jsScriptExecutor: JsScriptExecutor,
        httpReqDescList: MutableList<String>,
    ): HttpInfo {
        val start = System.currentTimeMillis()

        var bodyPublisher: HttpRequest.BodyPublisher? = null
        if (reqBody is String) {
            bodyPublisher = HttpRequest.BodyPublishers.ofString(reqBody)
        }
        val request = createRequest(url, version, reqHttpHeaders, bodyPublisher)

        val client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(6))
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofByteArray())

        val req = response.request()
        httpReqDescList.add(req.method() + " " + req.uri().toString() + " " + "\r\n")
        req.headers().map().forEach { entry ->
            entry.value.forEach {
                httpReqDescList.add(entry.key + ": " + it + "\r\n")
            }
        }
        httpReqDescList.add("\r\n")
        if (reqBody is String) {
            httpReqDescList.add(reqBody)
        }

        val size = response.body().size / 1024.0
        val consumeTimes = System.currentTimeMillis() - start

        val resHeaderList = convertToResHeaderDescList(response)

        val resPair = convertToResPair(response)

        val httpResDescList =
            mutableListOf("# status: ${response.statusCode()} 耗时: ${consumeTimes}ms 大小: ${size}kb\r\n")

        val evalJsRes = jsScriptExecutor.evalJsAfterRequest(jsScriptStr, response, resPair)
        if (!evalJsRes.isNullOrEmpty()) {
            httpResDescList.add("# 后置js执行结果:\r\n")
            httpResDescList.add("# $evalJsRes")
        }

        httpResDescList.add(req.method() + " " + response.uri().toString() + "\r\n")

        httpResDescList.addAll(resHeaderList)

        if (resPair.first != "image") {
            httpResDescList.add(String(resPair.second, StandardCharsets.UTF_8))
        }

        return HttpInfo(httpReqDescList, httpResDescList, resPair.first, resPair.second)
    }

    abstract fun createRequest(
        url: String,
        version: Version,
        reqHeaderMap: MutableMap<String, String>,
        bodyPublisher: HttpRequest.BodyPublisher?,
    ): HttpRequest

    companion object {
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

        fun getInstance(httpMethod: HttpMethod): HttpRequestEnum {
            val elementType = PsiUtilCore.getElementType(httpMethod.firstChild)
            if (elementType === HttpTypes.POST) {
                return POST
            } else if (elementType === HttpTypes.GET) {
                return GET
            }
            throw UnsupportedOperationException()
        }
    }
}
