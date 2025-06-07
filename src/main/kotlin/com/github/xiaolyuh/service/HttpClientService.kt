package com.github.xiaolyuh.service

import com.github.xiaolyuh.utils.GsonUtils.gson
import com.google.gson.JsonObject
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import org.apache.http.entity.ContentType
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.util.function.Consumer

/**
 * HttpClient工具
 */
@Service(Service.Level.PROJECT)
class HttpClientService(private val project: Project) {

    /**
     * 发起 application/json 的 post 请求
     *
     * @param url   地址
     * @param param 参数
     */
    fun <T> postApplicationJson(url: String, param: Any, resType: Class<T>) {
        val headers = mutableMapOf<String, String>()

        headers["Content-Type"] = ContentType.APPLICATION_JSON.mimeType
        headers["Accept"] = ContentType.APPLICATION_JSON.mimeType

        postForObject(url, gson.toJson(param), headers, resType)
    }

    fun <T> postJsonForObjectWithToken(
        url: String, reqBody: String, headers: MutableMap<String, String>, resType: Class<T>,
    ): T {
        headers["Content-Type"] = ContentType.APPLICATION_JSON.mimeType
        headers["Accept"] = ContentType.APPLICATION_JSON.mimeType

        return postForObjectWithToken(url, reqBody, headers, resType)
    }

    private fun <T> postForObjectWithToken(
        url: String, reqBody: String, headers: MutableMap<String, String>?, resType: Class<T>,
    ): T {
        var headersTmp = headers

        val configService = ConfigService.getInstance(project)

        val kubesphereToken = configService.getKubesphereToken()

        if (headersTmp == null) {
            headersTmp = mutableMapOf()
        }

        headersTmp["Cookie"] = "token=$kubesphereToken"

        return postForObject(url, reqBody, headersTmp, resType)
    }

    fun <T> postForObject(
        url: String, reqBody: String, headers: MutableMap<String, String>?, resType: Class<T>,
    ): T {
        val builder = HttpRequest.newBuilder()
            .POST(HttpRequest.BodyPublishers.ofString(reqBody, StandardCharsets.UTF_8))
            .uri(URI.create(url))

        return reqForObject(builder, headers, resType)
    }

    fun <T> getForObjectWithToken(
        url: String, headers: MutableMap<String, String>?, resType: Class<T>,
    ): T {
        var headersTmp = headers
        val configService = ConfigService.getInstance(project)

        val kubesphereToken = configService.getKubesphereToken()

        if (headersTmp == null) {
            headersTmp = mutableMapOf()
        }

        headersTmp["Cookie"] = "token=$kubesphereToken"

        return getForObject(url, headersTmp, resType)
    }

    private fun <T> getForObject(
        url: String, headers: MutableMap<String, String>, resType: Class<T>,
    ): T {
        val builder = HttpRequest.newBuilder()
            .uri(URI.create(url))

        return reqForObject(builder, headers, resType)
    }

    private fun <T> reqForObject(
        builder: HttpRequest.Builder, headers: MutableMap<String, String>?, resType: Class<T>,
    ): T {
        val client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(6))
            .build()

        headers?.forEach { (name, value) -> builder.setHeader(name, value) }

        val request = builder.timeout(Duration.ofSeconds(6)).build()

        @Suppress("UNCHECKED_CAST")
        val response = if (resType == ByteArray::class.java) {
            client.send(request, HttpResponse.BodyHandlers.ofByteArray()) as HttpResponse<T>
        } else {
            client.send(request, HttpResponse.BodyHandlers.ofString()) as HttpResponse<T>
        }

        val statusCode = response.statusCode()
        if (statusCode == 404) {
            throw RuntimeException("链接 404:" + request.uri())
        }

        val kubesphereService = KubesphereService.getInstance(project)

        if (statusCode == 401 || statusCode == 403) {
            if (kubesphereService.isLoginUrl(request.uri().toString())) {
                throw RuntimeException("用户名或密码无效," + response.body())
            }

            kubesphereService.loginAndSaveToken()

            return getForObjectWithToken(request.uri().toString(), headers, resType)
        }

        val cookies = response.headers().allValues("set-cookie")

        val resJson = handleGroupLogin(
            request.uri().toString(), project, statusCode,
            cookies, response.body().toString() + ""
        )
        if (resJson != null) {
            @Suppress("UNCHECKED_CAST")
            return resJson as T
        }

        val body = response.body()

        if (resType == String::class.java || resType == ByteArray::class.java) {
            return body
        }

        return gson.fromJson(body as String, resType)
    }

    private fun handleGroupLogin(
        url: String,
        project: Project,
        status: Int,
        cookies: List<String>,
        body: String,
    ): JsonObject? {
        val kubesphereService = KubesphereService.getInstance(project)

        val configService = ConfigService.getInstance(project)

        val isGroupLoginUrl = kubesphereService.isLoginUrl(url) && configService.isGroupKubesphere()
        if (!isGroupLoginUrl) {
            return null
        }

        if (status == 200) {
            throw RuntimeException("用户名或密码无效,$body")
        }

        if (status == 302) {
            val accessToken = kubesphereService.getTokenFromResponseCookie(cookies)
            val jsonObject = JsonObject()
            jsonObject.addProperty("access_token", accessToken)
            return jsonObject
        }

        return null
    }

    fun <T> getForObjectWithTokenUseUrl(
        url: String, headers: MutableMap<String, String>?, resType: Class<T>,
    ): T {
        var headersTmp = headers
        val configService = ConfigService.getInstance(project)

        val kubesphereToken = configService.getKubesphereToken()

        if (headersTmp == null) {
            headersTmp = mutableMapOf()
        }

        headersTmp["Cookie"] = "token=$kubesphereToken"

        return getForObjectUseUrl(url, headersTmp, resType)
    }

    fun <T> getForObjectWithTokenUseUrl(
        url: String, headers: MutableMap<String, String>?, resType: Class<T>, consumer: Consumer<T>,
    ) {
        var headersTmp = headers
        val configService = ConfigService.getInstance(project)

        val kubesphereToken = configService.getKubesphereToken()

        if (headersTmp == null) {
            headersTmp = mutableMapOf()
        }

        headersTmp["Cookie"] = "token=$kubesphereToken"

        getForObjectUseUrl(url, headersTmp, resType, consumer)
    }

    private fun <T> getForObjectUseUrl(
        url: String, headers: MutableMap<String, String>, resType: Class<T>,
    ): T {
        var connection: HttpURLConnection? = null
        var inputStream: InputStream? = null

        try {
            connection = URL(url).openConnection() as HttpURLConnection

            connection.requestMethod = "GET"

            for ((key, value) in headers) {
                connection.setRequestProperty(key, value)
            }

            connection.connect()

            val responseCode = connection.responseCode
            if (responseCode == 404) {
                throw RuntimeException("链接 404:$url")
            }

            if (responseCode == 401 || responseCode == 403) {
                val kubesphereService = KubesphereService.getInstance(project)

                if (kubesphereService.isLoginUrl(url)) {
                    inputStream = connection.inputStream
                    val bytes = inputStream.readAllBytes()
                    val body = String(bytes)
                    throw RuntimeException("用户名或密码无效,$body")
                }

                kubesphereService.loginAndSaveToken()

                return getForObjectWithTokenUseUrl(url, headers, resType)
            }

            val cookies = connection.headerFields["set-cookie"] ?: emptyList()

            val resJson = handleGroupLogin(url, project, responseCode, cookies, "请检查 Kubesphere 配置")
            if (resJson != null) {
                @Suppress("UNCHECKED_CAST")
                return resJson as T
            }

            inputStream = connection.inputStream

            val bytes = inputStream.readAllBytes()
            if (resType == ByteArray::class.java) {
                @Suppress("UNCHECKED_CAST")
                return bytes as T
            }

            val body = String(bytes)
            if (resType == String::class.java) {
                @Suppress("UNCHECKED_CAST")
                return body as T
            }

            return gson.fromJson(body, resType)
        } finally {
            inputStream?.close()
            connection?.disconnect()
        }
    }

    private fun <T> getForObjectUseUrl(
        url: String, headers: MutableMap<String, String>, resType: Class<T>, consumer: Consumer<T>,
    ) {
        var connection: HttpURLConnection? = null
        var inputStream: InputStream? = null
        try {
            connection = URL(url).openConnection() as HttpURLConnection

            connection.requestMethod = "GET"

            for ((key, value) in headers) {
                connection.setRequestProperty(key, value)
            }

            connection.connect()

            val responseCode = connection.responseCode

            if (responseCode == 404) {
                throw RuntimeException("链接 404:$url")
            }

            val kubesphereService = KubesphereService.getInstance(project)

            if (responseCode == 401 || responseCode == 403) {
                if (kubesphereService.isLoginUrl(url)) {
                    inputStream = connection.inputStream

                    val bytes = inputStream.readAllBytes()
                    val body = String(bytes)

                    throw RuntimeException("用户名或密码无效,$body")
                }

                kubesphereService.loginAndSaveToken()

                getForObjectWithTokenUseUrl(url, headers, resType)

                return
            }

            val cookies = connection.headerFields["set-cookie"] ?: emptyList()

            handleGroupLogin(url, project, responseCode, cookies, "请检查 Kubesphere 配置")

            while (!Thread.currentThread().isInterrupted) {
                inputStream = connection.inputStream

                val bytes = inputStream.readNBytes(128)

                if (!Thread.currentThread().isInterrupted) {
                    when (resType) {
                        ByteArray::class.java -> {
                            @Suppress("UNCHECKED_CAST")
                            consumer.accept(bytes as T)
                        }

                        String::class.java -> {
                            val body = String(bytes)
                            @Suppress("UNCHECKED_CAST")
                            consumer.accept(body as T)
                        }

                        else -> {
                            val body = String(bytes)
                            consumer.accept(gson.fromJson(body, resType))
                        }
                    }
                }
            }
        } finally {
            inputStream?.close()
            connection?.disconnect()
        }
    }

    companion object {
        fun getInstance(project: Project): HttpClientService {
            return project.getService(HttpClientService::class.java)
        }
    }
}
