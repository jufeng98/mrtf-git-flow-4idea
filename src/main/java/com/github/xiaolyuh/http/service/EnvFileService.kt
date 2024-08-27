package com.github.xiaolyuh.http.service

import com.github.xiaolyuh.utils.HttpClientUtil
import com.google.gson.JsonObject
import com.intellij.codeInspection.ui.actions.LOG
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import org.apache.commons.io.FileUtils
import java.io.File
import java.nio.charset.StandardCharsets
import java.util.*

/**
 * @author yudong
 */
@Service(Service.Level.PROJECT)
class EnvFileService(val project: Project) {
    private lateinit var envJsonObj: JsonObject
    private lateinit var privateEnvJsonObj: JsonObject

    init {
        initEnv()
    }

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

    private fun initEnv() {
        envJsonObj = initEnvObj(ENV_FILE_NAME)

        privateEnvJsonObj = initEnvObj(PRIVATE_ENV_FILE_NAME)
    }

    fun initEnv(fileName: String, content: CharSequence) {
        val jsonObject = HttpClientUtil.gson.fromJson(content.toString(), JsonObject::class.java)
        LOG.warn("环境文件发生变化,env size:" + jsonObject.size())
        if (Objects.equals(fileName, ENV_FILE_NAME)) {
            envJsonObj = jsonObject
        } else if (Objects.equals(fileName, PRIVATE_ENV_FILE_NAME)) {
            privateEnvJsonObj = jsonObject
        }
    }

    private fun initEnvObj(envFileName: String): JsonObject {
        val envFiles =
            FilenameIndex.getVirtualFilesByName(envFileName, GlobalSearchScope.projectScope(project))
        if (envFiles.isEmpty()) {
            return JsonObject()
        }

        val envFile = envFiles.iterator().next()

        return initEnvObj(envFile)
    }

    private fun initEnvObj(envFile: VirtualFile): JsonObject {
        val jsonStr = FileUtils.readFileToString(File(envFile.path), StandardCharsets.UTF_8)
        val jsonObject = HttpClientUtil.gson.fromJson(jsonStr, JsonObject::class.java)
        LOG.warn("完成初始化环境文件,env size:" + jsonObject.size())
        return jsonObject
    }

    companion object {
        const val ENV_FILE_NAME = "http-client.env.json"
        const val PRIVATE_ENV_FILE_NAME = "http-client.private.env.json"
        fun getService(project: Project): EnvFileService {
            return project.getService(EnvFileService::class.java)
        }
    }

}
