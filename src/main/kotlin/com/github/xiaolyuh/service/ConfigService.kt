package com.github.xiaolyuh.service

import com.github.xiaolyuh.config.InitOptions
import com.github.xiaolyuh.config.K8sOptions
import com.github.xiaolyuh.consts.Constants
import com.github.xiaolyuh.utils.GsonUtils.gson
import com.github.xiaolyuh.utils.StringUtils
import com.github.xiaolyuh.utils.VirtualFileUtils
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.psi.PsiDocumentManager
import com.intellij.util.application
import java.io.File
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.prefs.Preferences
import java.util.regex.Pattern

/**
 * 配置管理工具
 */
@Service(Service.Level.PROJECT)
class ConfigService(private val project: Project) {
    private val logger = Logger.getInstance(ConfigService::class.java)
    private val variablePattern = Pattern.compile("(\\{[^{}]+})")

    private val preferences: Preferences = Preferences.userRoot().node("com.github.xiaolyuh")

    private var initOptions: InitOptions? = null
    private var k8sOptions: K8sOptions? = null

    fun resolveOption(str: String, map: Map<String, String> = emptyMap()): String {
        val clazz = K8sOptions::class.java
        val matcher = variablePattern.matcher(str)

        return matcher.replaceAll {
            var variableName = it.group()
            variableName = variableName.substring(1, variableName.length - 1)

            val myValue = map[variableName]
            if (myValue != null) {
                return@replaceAll myValue
            }

            try {
                val field = clazz.getDeclaredField(variableName)
                field.isAccessible = true
                val value = field.get(k8sOptions!!)
                if (value !is String) {
                    throw IllegalArgumentException("$str 配置有误,$variableName 不是字符串类型!")
                }

                resolveOption(value, map)
            } catch (_: NoSuchFieldException) {
                throw IllegalArgumentException("$str 配置有误,$variableName 不存在,请检查!")
            }
        }
    }

    fun getInitOptionsNullable(): InitOptions? {
        return initOptions
    }

    fun getInitOptions(): InitOptions {
        return initOptions!!
    }

    fun getK8sOptions(): K8sOptions {
        return k8sOptions!!
    }

    fun getKubesphereUser(): Pair<String, String> {
        return Pair(preferences["kubesphereUsername", ""], preferences["kubespherePassword", ""])
    }

    fun saveKubesphereUser(name: String, pwd: String) {
        preferences.put("kubesphereUsername", name)
        preferences.put("kubespherePassword", pwd)
    }

    fun saveFsWebHookUrl(url: String) {
        preferences.put("fsWebHookUrl", url)
    }

    private val fsWebHookUrl = preferences["fsWebHookUrl", ""]

    fun getKubesphereToken(): String {
        return if (isMhKubesphere()) {
            preferences["kubesphereToken", "abed"]
        } else {
            preferences["kubesphereTokenGroup", "abed"]
        }
    }

    fun saveKubesphereToken(token: String) {
        if (isMhKubesphere()) {
            preferences.put("kubesphereToken", token)
        } else {
            preferences.put("kubesphereTokenGroup", token)
        }
    }

    /**
     * 将配置存储到本地项目空间
     *
     * @param configJson configJson
     */
    fun saveConfigToLocal(configJson: String) {
        // 存储到本地项目空间
        val component = PropertiesComponent.getInstance(project)

        component.setValue(Constants.KEY_PREFIX + project.name, configJson)

        initOptions = null
    }

    /**
     * 将配置存储到本地文件
     *
     * @param configJson configJson
     */
    fun saveConfigToFile(configJson: String, finished: Runnable) {
        val filePath = project.basePath + File.separator + Constants.CONFIG_FILE_NAME
        val file = File(filePath)
        if (!file.exists()) {
            file.createNewFile()
        }

        initOptions = null
        k8sOptions = null

        runInEdt {
            runWriteAction {
                val virtualFile = VfsUtil.findFileByIoFile(file, true)!!

                VfsUtil.saveText(virtualFile, configJson)

                PsiDocumentManager.getInstance(project)
                    .performWhenAllCommitted {
                        finished.run()
                    }
            }
        }
    }

    /**
     * 判断插件否初始化
     *
     * @return boolean 已初始化返回true，否则返回false
     */
    fun isInit(): Boolean {
        return initOptions != null
    }

    fun tryInitConfig() {
        application.executeOnPooledThread {
            var options = getFromProjectConfigFile()
            if (Objects.isNull(options)) {
                options = getFromProjectWorkspace()
            }

            if (Objects.nonNull(options)) {
                val pair = getKubesphereUser()
                options!!.kubesphereUsername = pair.first
                options.kubespherePassword = pair.second
                options.fsWebHookUrl = fsWebHookUrl
                initOptions = options
            }

            val k8sOptions = getFromProjectK8sFile()
            if (k8sOptions != null) {
                this.k8sOptions = k8sOptions
            }
        }
    }

    /**
     * 从本地项目空间获取配置
     */
    private fun getFromProjectWorkspace(): InitOptions? {
        val component = PropertiesComponent.getInstance(project)

        val key = Constants.KEY_PREFIX + project.name

        val json = component.getValue(key)
        if (StringUtils.isBlank(json)) {
            return null
        }

        logger.info("完成读取项目空间配置workspace.xml,key:$key")

        return gson.fromJson(json, InitOptions::class.java)
    }

    /**
     * 从配置文件获取配置
     */
    private fun getFromProjectConfigFile(): InitOptions? {
        val filePath = project.basePath + File.separator + Constants.CONFIG_FILE_NAME
        val file = File(filePath)

        val configJsonStr = VirtualFileUtils.readNewestContent(file)
        if (configJsonStr == null) {
            return null
        }

        logger.info("完成读取配置文件:$filePath")

        return gson.fromJson(configJsonStr, InitOptions::class.java)
    }

    fun existsK8sOptions(): Boolean {
        return k8sOptions != null
    }

    fun notExistsK8sOptions(): Boolean {
        return !existsK8sOptions()
    }

    fun getConsoleUrl(serviceName: String, instanceName: String): String {
        val k8sOptions = getK8sOptions()

        return resolveOption(
            k8sOptions.consoleUrl, mapOf(
                Pair("instanceName", instanceName),
                Pair("serviceName", serviceName),
            )
        )
    }

    fun getRunsUrl(mainTest: Boolean): String {
        val k8sOptions = getK8sOptions()

        var testBranch = URLEncoder.encode(initOptions!!.testBranch, StandardCharsets.UTF_8)

        testBranch = URLEncoder.encode(testBranch, StandardCharsets.UTF_8)

        return resolveOption(
            if (mainTest) {
                k8sOptions.runsUrl
            } else {
                k8sOptions.runsUrlSec
            }, mapOf(
                Pair("testBranch", testBranch),
            )
        )
    }

    fun getCompileLogUrl(id: String, mainTest: Boolean): String? {
        val k8sOptions = getK8sOptions()

        val url = if (mainTest) {
            k8sOptions.compileLogUrl
        } else {
            k8sOptions.compileLogUrlSec
        }

        if (url.isNullOrBlank()) {
            return null
        }

        var testBranch = URLEncoder.encode(
            if (mainTest) {
                initOptions!!.testBranch
            } else {
                initOptions!!.testBranchSec
            }, StandardCharsets.UTF_8
        )


        return resolveOption(
            url, mapOf(
                Pair("id", id),
                Pair("testBranch", testBranch)
            )
        )
    }

    fun getPushLogUrl(id: String, mainTest: Boolean): String {
        val k8sOptions = getK8sOptions()

        return resolveOption(
            if (mainTest) {
                k8sOptions.pushLogUrl
            } else {
                k8sOptions.pushLogUrlSec
            }, mapOf(Pair("id", id))
        )
    }

    fun getPodsUrl(serviceName: String, mainTest: Boolean): String {
        val k8sOptions = getK8sOptions()

        return resolveOption(
            if (mainTest) {
                k8sOptions.podsUrl
            } else {
                k8sOptions.podsUrlSec
            }, mapOf(Pair("serviceName", serviceName.lowercase()))
        )
    }

    fun getLogDir(serviceName: String): String {
        val k8sOptions = getK8sOptions()

        return resolveOption(k8sOptions.logDir, mapOf(Pair("selectService", serviceName)))
    }

    fun getLogsUrl(
        serviceName: String, instanceName: String, tailLines: Int,
        previous: Boolean, follow: Boolean, mainTest: Boolean,
    ): String {
        val k8sOptions = getK8sOptions()

        return resolveOption(
            if (mainTest) {
                k8sOptions.logsUrl
            } else {
                k8sOptions.logsUrlSec
            },
            mapOf(
                Pair("instanceName", instanceName),
                Pair("serviceName", serviceName.lowercase()),
                Pair("tailLines", "" + tailLines),
                Pair("follow", "" + follow),
                Pair("previous", "" + previous),
            ),
        )
    }

    fun getCrumbissuerUrl(): String {
        val k8sOptions = getK8sOptions()

        return resolveOption(k8sOptions.crumbissuerUrl)
    }

    fun isGroupKubesphere(): Boolean {
        return !isMhKubesphere()
    }

    fun isMhKubesphere(): Boolean {
        val k8sOptions = getK8sOptions()

        val host = k8sOptions.host

        return host.contains("mh")
    }

    fun getHost(): String {
        val k8sOptions = getK8sOptions()

        return k8sOptions.host
    }

    fun getLoginUrl(): String {
        val k8sOptions = getK8sOptions()

        return if (isMhKubesphere()) {
            k8sOptions.loginUrl
        } else {
            k8sOptions.loginUrlGroup
        }
    }

    private fun getFromProjectK8sFile(): K8sOptions? {
        val filePath = project.basePath + File.separator + Constants.CONFIG_FILE_NAME_PROJECT
        val file = File(filePath)

        val configJsonStr = VirtualFileUtils.readNewestContent(file)
        if (configJsonStr == null) {
            return null
        }

        logger.info("完成读取k8s配置文件:$filePath")

        return gson.fromJson(configJsonStr, K8sOptions::class.java)
    }

    companion object {
        fun getInstance(project: Project): ConfigService {
            return project.getService(ConfigService::class.java)
        }
    }
}
