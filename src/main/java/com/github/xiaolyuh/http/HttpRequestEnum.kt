package com.github.xiaolyuh.http

import com.github.xiaolyuh.http.psi.*
import com.github.xiaolyuh.utils.HttpClientUtil
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.intellij.psi.util.PsiUtilCore
import com.intellij.psi.util.elementType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpClient.Version
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import java.time.Duration
import javax.imageio.ImageIO
import kotlin.jvm.optionals.getOrElse

enum class HttpRequestEnum {
    GET {
        override fun createRequest(
            url: String,
            version: Version,
            reqHeaderMap: MutableMap<String, String>,
            bodyPublisher: HttpRequest.BodyPublisher?
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
            bodyPublisher: HttpRequest.BodyPublisher?
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

    suspend fun executeAsync(httpUrl: HttpUrl, httpHeaders: HttpHeaders?, httpBody: HttpBody?): Pair<List<String>, Any> {
        val url = httpUrl.text
        val version = Version.HTTP_1_1

        val headerMap = convertToReqHeaderMap(httpHeaders)

        val bodyPublisher = convertToReqBodyPublisher(httpBody)

        val request = createRequest(url, version, headerMap, bodyPublisher)

        val client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(6))
            .build()

        val response = withContext(Dispatchers.IO) {
            client.send(request, HttpResponse.BodyHandlers.ofByteArray())
        }

        val headerDescList = convertToResHeaderDescList(response)
        val resObj = convertToResObj(response)

        return Pair(headerDescList, resObj)
    }

    abstract fun createRequest(
        url: String,
        version: Version,
        reqHeaderMap: MutableMap<String, String>,
        bodyPublisher: HttpRequest.BodyPublisher?
    ): HttpRequest

    companion object {
        fun convertToReqHeaderMap(httpHeaders: HttpHeaders?): MutableMap<String, String> {
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
                        map[split[0]] = split[1]
                    }
            }
            return map
        }

        fun convertToReqBodyPublisher(httpBody: HttpBody?): HttpRequest.BodyPublisher? {
            if (httpBody == null) {
                return null
            }

            val ordinaryContent = httpBody.ordinaryContent
            if (ordinaryContent != null) {
                val elementType = ordinaryContent.firstChild.elementType
                if (elementType == HttpTypes.JSON_TEXT || elementType == HttpTypes.XML_TEXT) {
                    return HttpRequest.BodyPublishers.ofString(ordinaryContent.text, StandardCharsets.UTF_8)
                }
            }

            return null
        }

        fun convertToResHeaderDescList(response: HttpResponse<ByteArray>): MutableList<String> {
            val headerDescList = mutableListOf<String>()
            headerDescList.add(response.version().name + " " + response.statusCode())
            val headers = response.headers()
            headers.map()
                .forEach { (t, u) ->
                    u.forEach {
                        headerDescList.add("$t : $it")
                    }
                }
            return headerDescList
        }

        fun convertToResObj(response: HttpResponse<ByteArray>): Any {
            val resBody = response.body()
            val resHeaders = response.headers()
            val contentType = resHeaders.firstValue(org.apache.http.HttpHeaders.CONTENT_TYPE).getOrElse { "text/plain" }

            if (contentType.contains("json")) {
                val jsonStr = String(resBody, StandardCharsets.UTF_8)
                return if (jsonStr.startsWith("{")) {
                    HttpClientUtil.gson.fromJson(jsonStr, JsonObject::class.java)
                } else {
                    HttpClientUtil.gson.fromJson(jsonStr, JsonArray::class.java)
                }
            }

            if (contentType.contains("jpg") || contentType.contains("jpeg") || contentType.contains("png")) {
                return ImageIO.read(resBody.inputStream())
            }

            return String(resBody, StandardCharsets.UTF_8)
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
