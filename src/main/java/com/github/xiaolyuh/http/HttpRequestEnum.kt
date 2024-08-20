package com.github.xiaolyuh.http

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
import javax.imageio.ImageIO
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
        bodyPublisher: HttpRequest.BodyPublisher?,
        variableMap: MutableMap<String, Any>,
    ): Pair<List<String>, Any> {
        val start = System.currentTimeMillis()

        val request = createRequest(url, version, reqHttpHeaders, bodyPublisher)

        val client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(6))
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofByteArray())
        val size = response.body().size / 1024.0

        val headerDescList = convertToResHeaderDescList(response, variableMap)
        val resObj = convertToResObj(response)

        val consumeTimes = System.currentTimeMillis() - start
        headerDescList.add(
            0,
            "版本:${response.version().name} status:${response.statusCode()} 耗时:${consumeTimes}ms 大小:${size}kb\r\n\r\n"
        )

        return Pair(headerDescList, resObj)
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

        fun convertToReqBodyPublisher(httpBody: HttpBody?, variableResolver: VariableResolver): HttpRequest.BodyPublisher? {
            if (httpBody == null) {
                return null
            }

            val ordinaryContent = httpBody.ordinaryContent
            if (ordinaryContent != null) {
                val elementType = ordinaryContent.firstChild.elementType
                if (elementType == HttpTypes.JSON_TEXT || elementType == HttpTypes.XML_TEXT) {
                    val resolve = variableResolver.resolve(ordinaryContent.text)
                    return HttpRequest.BodyPublishers.ofString(resolve, StandardCharsets.UTF_8)
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
                    val resolve = variableResolver.resolve(str)
                    return HttpRequest.BodyPublishers.ofString(resolve, StandardCharsets.UTF_8)
                }
            }

            return null
        }

        fun convertToResHeaderDescList(
            response: HttpResponse<ByteArray>,
            variableMap: MutableMap<String, Any>,
        ): MutableList<String> {
            val headerDescList = mutableListOf<String>()

            val uri = response.uri()
            headerDescList.add("host:${uri.host}\r\n")
            headerDescList.add("path:${uri.path}\r\n")
            headerDescList.add("query:${uri.query ?: ""}\r\n")
            headerDescList.add("\r\n")

            if (variableMap.isNotEmpty()) {
                headerDescList.add("变量\r\n")
                variableMap.forEach {
                    headerDescList.add("${it.key} = ${it.value}\r\n")
                }
                headerDescList.add("\r\n")
            }

            val headers = response.headers()
            headers.map()
                .forEach { (t, u) ->
                    u.forEach {
                        headerDescList.add("$t : $it\r\n")
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
                return ImageIO.read(resBody.inputStream())
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
