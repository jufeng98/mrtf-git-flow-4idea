package com.github.xiaolyuh.logger

import com.intellij.openapi.diagnostic.Logger

/**
 * @author yudong
 */
object GitFlowPlusLogger {
    private val log = Logger.getInstance("GitFlowPlus")

    fun logWarn(msg: String, t: Throwable? = null) {
        val currentThread = Thread.currentThread()

        System.err.println("[${currentThread.id} ${currentThread.name}] GitFlowPlus: $msg")
        t?.printStackTrace()

        log.info(msg, t)
    }

    fun logInfo(msg: String?) {
        val currentThread = Thread.currentThread()

        println("[${currentThread.id} ${currentThread.name}] GitFlowPlus: $msg")

        log.info(msg)
    }

}