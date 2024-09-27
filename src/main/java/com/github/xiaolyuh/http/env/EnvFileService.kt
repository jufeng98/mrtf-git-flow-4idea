package com.github.xiaolyuh.http.env

import com.intellij.codeInspection.ui.actions.LOG
import com.intellij.json.psi.*
import com.intellij.openapi.components.Service
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileContentChangeEvent
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiUtil
import java.io.File


/**
 * @author yudong
 */
@Service(Service.Level.PROJECT)
class EnvFileService(val project: Project) {
    private var envMap: MutableMap<String, String> = mutableMapOf()

    init {
        project.messageBus.connect().subscribe(
            VirtualFileManager.VFS_CHANGES,
            object : BulkFileListener {

                override fun after(events: MutableList<out VFileEvent>) {
                    events.forEach {
                        if (it is VFileContentChangeEvent) {
                            return@forEach
                        }

                        val virtualFile = it.file ?: return@forEach
                        val fileName = virtualFile.name

                        if (!ENV_FILE_SET.contains(fileName)) {
                            return@forEach
                        }

                        val module = ModuleUtil.findModuleForFile(virtualFile, project) ?: return@forEach
                        val key = project.name + "-" + module.name + "-" + ENV_FILE_NAME
                        val remove = envMap.remove(key)
                        LOG.warn("清除env key缓存:${key},结果:${remove}")

                        if (remove != null) {
                            // 重新初始化 envMap 对象
                            getPresetEnvList(module)
                        }
                    }
                }
            })

    }

    fun getPresetEnvList(module: Module): MutableSet<String> {
        val keySet1 = collectEnvNames(ENV_FILE_NAME, module)

        val keySet2 = collectEnvNames(PRIVATE_ENV_FILE_NAME, module)

        val set = mutableSetOf<String>()
        set.addAll(keySet1)
        set.addAll(keySet2)

        set.remove(COMMON_ENV_NAME)

        return set
    }

    private fun collectEnvNames(envFileName: String, module: Module): Set<String> {
        val projectScope = GlobalSearchScope.projectScope(project)

        val key = project.name + "-" + module.name + "-" + envFileName

        val fileName = envMap[key]
        if (!fileName.isNullOrBlank()) {
            val virtualFile = VfsUtil.findFileByIoFile(File(fileName), true)!!
            return collectEnvNames(virtualFile)
        }

        val virtualFiles = FilenameIndex.getVirtualFilesByName(envFileName, projectScope)
        val virtualFile = virtualFiles.firstOrNull {
            module == ModuleUtil.findModuleForFile(it, project)
        }

        if (virtualFile == null) {
            envMap[key] = ""
            LOG.warn("初始化env key空缓存:${key}")
            return setOf()
        }

        envMap[key] = virtualFile.path
        LOG.warn("初始化env key缓存:${key},结果:${virtualFile.path}")

        return collectEnvNames(virtualFile)
    }

    private fun collectEnvNames(virtualFile: VirtualFile): Set<String> {
        val privateJsonFile = PsiUtil.getPsiFile(project, virtualFile) as JsonFile
        val jsonValue = privateJsonFile.topLevelValue

        if (jsonValue !is JsonObject) {
            throw IllegalArgumentException("配置文件:${virtualFile.name}格式不符合规范!")
        }

        val propertyList = jsonValue.propertyList
        val names = propertyList.map { it.name }.toList()

        return LinkedHashSet(names)
    }

    fun getEnvValue(key: String, selectedEnv: String?, module: Module): String? {
        var envValue = getEnvValue(key, selectedEnv, module, PRIVATE_ENV_FILE_NAME)
        if (envValue != null) {
            return envValue
        }

        envValue = getEnvValue(key, selectedEnv, module, ENV_FILE_NAME)
        if (envValue != null) {
            return envValue
        }

        envValue = getEnvValue(key, COMMON_ENV_NAME, module, PRIVATE_ENV_FILE_NAME)
        if (envValue != null) {
            return envValue
        }

        return getEnvValue(key, COMMON_ENV_NAME, module, ENV_FILE_NAME)
    }

    private fun getEnvValue(key: String, selectedEnv: String?, module: Module, envFileName: String): String? {
        val env = selectedEnv ?: COMMON_ENV_NAME

        val fileName = envMap[project.name + "-" + module.name + "-" + envFileName] ?: return null

        val virtualFile = VfsUtil.findFileByIoFile(File(fileName), true)
            ?: throw IllegalArgumentException("无法解析变量:${key},配置文件:${fileName}已不存在!")

        val psiFile = PsiUtil.getPsiFile(project, virtualFile)
        val jsonFile = psiFile as JsonFile
        val topLevelValue = jsonFile.topLevelValue
        if (topLevelValue !is JsonObject) {
            throw IllegalArgumentException("配置文件:${fileName}外层格式不符合规范!")
        }

        val envProperty = topLevelValue.findProperty(env) ?: throw IllegalArgumentException("环境:${env}不存在!")
        val jsonValue = envProperty.value
        if (jsonValue !is JsonObject) {
            throw IllegalArgumentException("配置文件:${fileName}内层格式不符合规范!")
        }

        val jsonProperty = jsonValue.findProperty(key) ?: return null

        val innerJsonValue = jsonProperty.value ?: return null
        return when (innerJsonValue) {
            is JsonStringLiteral -> {
                innerJsonValue.value
            }

            is JsonNumberLiteral -> {
                innerJsonValue.value.toString()
            }

            is JsonBooleanLiteral -> {
                innerJsonValue.value.toString()
            }

            else -> {
                throw IllegalArgumentException("配置文件:${fileName}最内层格式不符合规范!")
            }
        }
    }

    companion object {
        const val ENV_FILE_NAME = "http-client.env.json"
        const val PRIVATE_ENV_FILE_NAME = "http-client.private.env.json"

        val ENV_FILE_SET = setOf(ENV_FILE_NAME, PRIVATE_ENV_FILE_NAME)

        const val COMMON_ENV_NAME = "common"

        fun getService(project: Project): EnvFileService {
            return project.getService(EnvFileService::class.java)
        }
    }

}
