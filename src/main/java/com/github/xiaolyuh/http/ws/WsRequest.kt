package com.github.xiaolyuh.http.ws

import com.intellij.codeInspection.ui.actions.LOG
import com.intellij.openapi.project.Project
import java.net.URI
import java.net.http.HttpClient
import java.net.http.WebSocket
import java.nio.ByteBuffer
import java.time.Duration
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage


class WsRequest(val url: String, private val reqHeaderMap: MutableMap<String, String>, val project: Project) {
    private var webSocket: WebSocket? = null

    fun handleWsConnect() {
        connect()
    }

    private fun connect() {
        val uri = URI(url)

        val client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(6))
            .build()

        val builder = client.newWebSocketBuilder()
        reqHeaderMap.forEach { builder.header(it.key, it.value) }

        val listener = WsListener()

        builder.buildAsync(uri, listener)
            .whenComplete { t, u ->
                if (u != null) {
                    return@whenComplete
                }
                webSocket = t
            }
    }

    fun abortConnect() {
        webSocket?.abort()
        LOG.warn("关闭ws连接:$webSocket")
    }

    fun sendMsg(msg: String) {
        webSocket?.sendText(msg, true)
    }
}

class WsListener : WebSocket.Listener {
    override fun onText(webSocket: WebSocket?, data: CharSequence?, last: Boolean): CompletionStage<*> {
        LOG.warn("收到ws消息:$data")
        return CompletableFuture<Void>()
    }

    override fun onBinary(webSocket: WebSocket?, data: ByteBuffer?, last: Boolean): CompletionStage<*> {
        LOG.warn("收到ws二进制消息:$data")
        return CompletableFuture<Void>()
    }

    override fun onOpen(webSocket: WebSocket?) {
        LOG.warn("打开ws连接:$webSocket")
    }

    override fun onClose(webSocket: WebSocket?, statusCode: Int, reason: String?): CompletionStage<*> {
        LOG.warn("关闭ws连接:$webSocket,$statusCode,$reason")
        return CompletableFuture<Void>()
    }

    override fun onError(webSocket: WebSocket?, error: Throwable?) {
        LOG.warn("连接ws异常:$webSocket", error)
    }
}
