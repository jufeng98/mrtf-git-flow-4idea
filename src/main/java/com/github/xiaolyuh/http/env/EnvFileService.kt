package com.github.xiaolyuh.http.env

import com.github.xiaolyuh.utils.HttpClientUtil
import com.google.gson.JsonObject
import com.intellij.codeInspection.ui.actions.LOG
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import org.apache.commons.io.FileUtils
import java.io.File
import java.nio.charset.StandardCharsets
import java.util.*

/**
 * @author yudong
 */
@Service(Service.Level.PROJECT)
class EnvFileService(val project: Project) {
    private var initialed = false

    private var envJsonObj: JsonObject = JsonObject()
    private var privateEnvJsonObj: JsonObject = JsonObject()

    fun getPresetEnvList(): Set<String> {
        val keySet1 = envJsonObj.keySet()
        val keySet2 = privateEnvJsonObj.keySet()
        val set = mutableSetOf<String>()
        set.addAll(keySet1)
        set.addAll(keySet2)
        return set
    }

    fun getEnvValue(key: String, selectedEnv: String?): String? {
        if (selectedEnv.isNullOrEmpty()) {
            return null
        }

        val envValue = getEnvValue(key, selectedEnv, privateEnvJsonObj)
        if (envValue != null) {
            return envValue
        }

        return getEnvValue(key, selectedEnv, envJsonObj)
    }

    private fun getEnvValue(key: String, selectedEnv: String?, envObject: JsonObject): String? {
        val jsonObj: JsonObject = envObject.getAsJsonObject(selectedEnv) ?: return null
        val str = jsonObj.get(key)?.asString ?: return null
        return str
    }

    fun initEnv(envDir: String) {
        if (initialed) {
            return
        }
        initialed = true

        var file = File(envDir, ENV_FILE_NAME)
        if (file.exists()) {
            val jsonStr = FileUtils.readFileToString(file, StandardCharsets.UTF_8)
            initEnv(ENV_FILE_NAME, jsonStr)
        }

        file = File(envDir, PRIVATE_ENV_FILE_NAME)
        if (file.exists()) {
            val jsonStr = FileUtils.readFileToString(file, StandardCharsets.UTF_8)
            initEnv(PRIVATE_ENV_FILE_NAME, jsonStr)
        }
    }

    fun initEnv(fileName: String, content: CharSequence) {
        val jsonObject = HttpClientUtil.gson.fromJson(content.toString(), JsonObject::class.java)
        LOG.warn("初始化环境,env size:" + jsonObject.size())
        if (Objects.equals(fileName, ENV_FILE_NAME)) {
            envJsonObj = jsonObject
        } else if (Objects.equals(fileName, PRIVATE_ENV_FILE_NAME)) {
            privateEnvJsonObj = jsonObject
        }
    }

    companion object {
        const val ENV_FILE_NAME = "http-client.env.json"
        const val PRIVATE_ENV_FILE_NAME = "http-client.private.env.json"
        fun getService(project: Project): EnvFileService {
            return project.getService(EnvFileService::class.java)
        }
    }

}
