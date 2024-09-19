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
        var keySet1 = envJsonObj.keySet()
        keySet1 = LinkedHashSet(keySet1)
        keySet1.remove(COMMON_ENV_NAME)

        var keySet2 = privateEnvJsonObj.keySet()
        keySet2 = LinkedHashSet(keySet2)
        keySet2.remove(COMMON_ENV_NAME)

        val set = mutableSetOf<String>()
        set.addAll(keySet1)
        set.addAll(keySet2)
        return set
    }

    fun getEnvValue(key: String, selectedEnv: String?): String? {
        var envValue = getEnvValue(key, selectedEnv, privateEnvJsonObj)
        if (envValue != null) {
            return envValue
        }

        envValue = getEnvValue(key, selectedEnv, envJsonObj)
        if (envValue != null) {
            return envValue
        }

        envValue = getEnvValue(key, COMMON_ENV_NAME, privateEnvJsonObj)
        if (envValue != null) {
            return envValue
        }

        return getEnvValue(key, COMMON_ENV_NAME, envJsonObj)
    }

    private fun getEnvValue(key: String, selectedEnv: String?, envObject: JsonObject): String? {
        val jsonObj: JsonObject = envObject.getAsJsonObject(selectedEnv) ?: return null
        return jsonObj.get(key)?.asString
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
        val jsonObject: JsonObject
        try {
            jsonObject = HttpClientUtil.gson.fromJson(content.toString(), JsonObject::class.java)
        } catch (e: Exception) {
            return
        }

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
        const val COMMON_ENV_NAME = "common"
        fun getService(project: Project): EnvFileService {
            return project.getService(EnvFileService::class.java)
        }
    }

}
