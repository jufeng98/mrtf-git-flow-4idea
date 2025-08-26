package com.github.xiaolyuh.service

import com.github.xiaolyuh.config.InitOptions
import com.github.xiaolyuh.config.K8sOptions
import com.github.xiaolyuh.consts.Constants
import com.github.xiaolyuh.utils.GsonUtils.gson
import com.github.xiaolyuh.utils.StringUtils
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.readText
import java.io.File
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.text.MessageFormat
import java.util.*
import java.util.prefs.Preferences

/**
 * 配置管理工具
 */
@Service(Service.Level.PROJECT)
class ConfigService(private val project: Project) {
    private val logger = Logger.getInstance(ConfigService::class.java)

    private val preferences: Preferences = Preferences.userRoot().node("com.github.xiaolyuh")

    private var initOptions: InitOptions? = null
    private var k8sOptions: K8sOptions? = null

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

        val virtualFile = VfsUtil.findFileByIoFile(file, true)!!

        initOptions = null
        k8sOptions = null

        runInEdt {
            runWriteAction {
                VfsUtil.saveText(virtualFile, configJson)

                finished.run()
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

        val virtualFile = VfsUtil.findFileByIoFile(file, true)

        if (virtualFile == null) {
            logger.info(filePath + "不存在")
            return null
        }

        val configJsonStr = virtualFile.readText()
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

        val consoleUrl = k8sOptions.host + k8sOptions.consolePath

        return MessageFormat.format(consoleUrl, k8sOptions.cluster, k8sOptions.namespace, instanceName, serviceName)
    }

    fun getRunsUrl(): String {
        val k8sOptions = getK8sOptions()

        val runsUrl = k8sOptions.host + k8sOptions.runsPath

        var testBranch = URLEncoder.encode(initOptions!!.testBranch, StandardCharsets.UTF_8)

        testBranch = URLEncoder.encode(testBranch, StandardCharsets.UTF_8)

        val pipelines = k8sOptions.pipelines

        val str = if (StringUtils.isNotBlank(pipelines)) pipelines else k8sOptions.namespace

        return MessageFormat.format(runsUrl, k8sOptions.cluster, str, testBranch)
    }

    fun getCompileLogPath(): String? {
        val k8sOptions = getK8sOptions()

        return k8sOptions.compileLogPath
    }

    fun getPodsUrl(serviceName: String): String {
        val k8sOptions = getK8sOptions()

        val podsUrl = k8sOptions.host + k8sOptions.podsPath

        return MessageFormat.format(
            podsUrl,
            k8sOptions.cluster,
            k8sOptions.namespace,
            serviceName.lowercase()
        )
    }

    fun getLogsUrl(
        serviceName: String, instanceName: String, tailLines: Int,
        previous: Boolean, follow: Boolean,
    ): String {
        val k8sOptions = getK8sOptions()

        val logsUrl = k8sOptions.host + k8sOptions.logsPath

        return MessageFormat.format(
            logsUrl, k8sOptions.cluster, k8sOptions.namespace, instanceName,
            serviceName.lowercase(), "" + tailLines, follow, previous
        )
    }

    fun getCrumbissuerUrl(): String {
        val k8sOptions = getK8sOptions()

        val podsUrl = k8sOptions.host + k8sOptions.crumbissuerPath

        return MessageFormat.format(podsUrl, k8sOptions.cluster)
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
            // 集团登录接口
            k8sOptions.host + "/login"
        }
    }

    private fun getFromProjectK8sFile(): K8sOptions? {
        val filePath = project.basePath + File.separator + Constants.CONFIG_FILE_NAME_PROJECT
        val file = File(filePath)

        val virtualFile = VfsUtil.findFileByIoFile(file, true)
        if (virtualFile == null) {
            logger.info(filePath + "不存在")
            return null
        }

        val configJsonStr = virtualFile.readText()

        return gson.fromJson(configJsonStr, K8sOptions::class.java)
    }

    companion object {
        fun getInstance(project: Project): ConfigService {
            return project.getService(ConfigService::class.java)
        }
    }
}
