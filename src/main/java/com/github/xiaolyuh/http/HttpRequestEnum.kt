package com.github.xiaolyuh.http

import com.github.xiaolyuh.http.js.JsScriptExecutor
import com.github.xiaolyuh.http.psi.HttpMethod
import com.github.xiaolyuh.http.psi.HttpTypes
import com.github.xiaolyuh.utils.HttpUtils.convertToResHeaderDescList
import com.github.xiaolyuh.utils.HttpUtils.convertToResPair
import com.intellij.psi.util.PsiUtilCore
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpClient.Version
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import java.time.Duration

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
        } else if (reqBody is List<*>) {
            @Suppress("UNCHECKED_CAST")
            bodyPublisher = HttpRequest.BodyPublishers.ofByteArrays(reqBody as MutableIterable<ByteArray>)
        }

        val request = createRequest(url, version, reqHttpHeaders, bodyPublisher)

        val client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(6))
            .build()

        httpReqDescList.add(request.method() + " " + request.uri().toString() + " " + "\r\n")
        request.headers()
            .map()
            .forEach { entry ->
                entry.value.forEach {
                    httpReqDescList.add(entry.key + ": " + it + "\r\n")
                }
            }

        if (bodyPublisher != null) {
            httpReqDescList.add("Content-Length: " + bodyPublisher.contentLength() + "\r\n")
        }
        httpReqDescList.add("\r\n")

        if (reqBody is String) {
            httpReqDescList.add(reqBody)
        } else if (reqBody is List<*>) {
            @Suppress("UNCHECKED_CAST")
            val byteArrays = reqBody as MutableIterable<ByteArray>
            byteArrays.forEach {
                if (it.size > 100) {
                    val bytes = it.copyOfRange(0, 100)
                    httpReqDescList.add(String(bytes) + " ......\r\n(内容过长,忽略显示后面部分)\r\n")
                } else {
                    httpReqDescList.add(String(it))
                }
            }
        }

        val response: HttpResponse<ByteArray>?
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofByteArray())
        } catch (e: Exception) {
            return HttpInfo(httpReqDescList, mutableListOf(), null, null, e)
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
            httpResDescList.add("$evalJsRes")
        }

        httpResDescList.add(request.method() + " " + response.uri().toString() + "\r\n")

        httpResDescList.addAll(resHeaderList)

        if (resPair.first != "image") {
            httpResDescList.add(String(resPair.second, StandardCharsets.UTF_8))
        }

        return HttpInfo(httpReqDescList, httpResDescList, resPair.first, resPair.second, null)
    }

    abstract fun createRequest(
        url: String,
        version: Version,
        reqHeaderMap: MutableMap<String, String>,
        bodyPublisher: HttpRequest.BodyPublisher?,
    ): HttpRequest

    companion object {
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
