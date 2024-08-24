package com.github.xiaolyuh.http.service

import com.github.xiaolyuh.utils.HttpClientUtil
import com.google.gson.JsonObject
import com.intellij.codeInspection.ui.actions.LOG
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import org.apache.commons.io.FileUtils
import java.io.File
import java.nio.charset.StandardCharsets

/**
 * @author yudong
 */
@Service(Service.Level.PROJECT)
class EnvFileService(val project: Project) {
    private lateinit var envJsonObj: JsonObject
    private lateinit var privateEnvJsonObj: JsonObject

    private val envFileName = "http-client.env.json"
    private val privateEnvFileName = "http-client.private.env.json"

    init {
        initEnv()

        project.messageBus.connect().subscribe(
            VirtualFileManager.VFS_CHANGES,
            object : BulkFileListener {

                override fun after(events: MutableList<out VFileEvent>) {
                    events.forEach {
                        if (envFileName == it.file?.name) {
                            envJsonObj = initEnvObj(it.file!!)
                        } else if (privateEnvFileName == it.file?.name) {
                            privateEnvJsonObj = initEnvObj(it.file!!)
                        }
                    }
                }

            })
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
        envJsonObj = initEnvObj(envFileName)

        privateEnvJsonObj = initEnvObj(privateEnvFileName)
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
        fun getService(project: Project): EnvFileService {
            return project.getService(EnvFileService::class.java)
        }
    }

}
