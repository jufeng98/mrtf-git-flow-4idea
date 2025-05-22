package com.github.xiaolyuh.service

import com.github.xiaolyuh.utils.ExecutorUtils.monitorBuildTask
import com.github.xiaolyuh.utils.HttpClientUtil
import com.github.xiaolyuh.utils.NotifyUtil
import com.github.xiaolyuh.utils.StringUtils
import com.github.xiaolyuh.vo.InstanceVo
import com.google.common.collect.Maps
import com.google.gson.JsonObject
import com.intellij.ide.impl.ProjectUtil.getActiveProject
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Pair
import org.apache.commons.lang3.exception.ExceptionUtils
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

@Service(Service.Level.PROJECT)
class KubesphereService(private val project: Project) {

    fun triggerPipeline(selectService: String) {
        if (StringUtils.isBlank(selectService)) {
            NotifyUtil.notifyInfo(project, "未选择服务,跳过触发流水线")
            return
        }

        val configService = ConfigService.getInstance(project)

        if (configService.notExistsK8sOptions()) {
            NotifyUtil.notifyInfo(project, "缺少k8s配置文件,跳过触发流水线")
            return
        }

        val crumbissuerUrl = configService.getCrumbissuerUrl()
        var resObj = HttpClientUtil.getForObjectWithToken(
            crumbissuerUrl, null,
            JsonObject::class.java, project
        )
        val crumb = resObj["crumb"].asString
        NotifyUtil.notifyInfo(project, "请求url:$crumbissuerUrl,结果crumb:$crumb")
        if (StringUtils.isBlank(crumb)) {
            throw RuntimeException(crumbissuerUrl + "配置有误:" + resObj)
        }

        val isFrontProject = configService.getK8sOptions().isFrontProject
        val reqBody = if (isFrontProject) {
            "{\"parameters\":[]}"
        } else {
            String.format("{\"parameters\":[{\"name\":\"MDL_NAME\",\"value\":\"%s\"}]}", selectService)
        }

        val runsUrl = configService.getRunsUrl()
        val headers: MutableMap<String, String> = Maps.newHashMap()
        headers["Jenkins-Crumb"] = crumb
        resObj = HttpClientUtil.postJsonForObjectWithToken(
            runsUrl, reqBody, headers,
            JsonObject::class.java, project
        )
        if (!resObj.has("id")) {
            throw RuntimeException(runsUrl + "配置有误:" + resObj)
        }

        val id = resObj["id"].asString
        val msg = String.format(
            "请求url:%s,结果id:%s,queueId:%s,state:%s", runsUrl, id,
            resObj["queueId"].asString, resObj["state"].asString
        )
        NotifyUtil.notifyInfo(project, msg)

        NotifyUtil.notifyInfo(project, selectService + "触发流水线成功!")

        monitorBuildTask(id, selectService, project)

        NotifyUtil.notifyInfo(project, "开始监控" + selectService + " id为" + id + "的构建情况")
    }

    fun loginAndSaveToken() {
        val configService = ConfigService.getInstance(project)
        val pair = configService.kubesphereUser
        val kubesphereUsername = pair.first
        if (StringUtils.isBlank(kubesphereUsername)) {
            throw RuntimeException("请先在配置菜单配置Kubesphere用户信息")
        }
        val kubespherePassword = pair.second

        val accessToken = loginByUrl(kubesphereUsername, kubespherePassword)

        configService.saveKubesphereToken(accessToken)
    }

    private fun loginByUrl(kubesphereUsername: String, kubespherePassword: String): String {
        val configService = ConfigService.getInstance(project)
        val loginUrl = configService.getLoginUrl()
        val accessToken: String
        if (configService.isMhKubesphere()) {
            val reqBody =
                String.format("grant_type=password&username=%s&password=%s", kubesphereUsername, kubespherePassword)
            val headers: MutableMap<String, String> = Maps.newHashMap()
            headers["Content-Type"] = "application/x-www-form-urlencoded"
            val jsonObject = HttpClientUtil.postForObject(
                loginUrl, reqBody, headers,
                JsonObject::class.java, project
            )
            accessToken = jsonObject["access_token"].asString
        } else {
            val reqJsonObj = JsonObject()
            reqJsonObj.addProperty("username", kubesphereUsername)

            val encrypt = encrypt("kubesphere", kubespherePassword)
            reqJsonObj.addProperty("encrypt", encrypt)

            val reqBody = reqJsonObj.toString()
            val reqHeaders: MutableMap<String, String> = Maps.newHashMap()
            reqHeaders["Content-Type"] = "application/json"
            reqHeaders["Accept"] = "*/*"
            val jsonObject = HttpClientUtil.postForObject(
                loginUrl, reqBody, reqHeaders,
                JsonObject::class.java, project
            )
            accessToken = jsonObject["access_token"].asString
        }

        NotifyUtil.notifyInfo(
            getActiveProject(),
            "请求url:" + loginUrl + "," + kubesphereUsername + "登录结果accessToken:" + accessToken
        )
        return accessToken
    }

    fun isLoginUrl(url: String): Boolean {
        val configService = ConfigService.getInstance(project)
        return url == configService.getLoginUrl()
    }

    fun getTokenFromResponseCookie(cookies: List<String>): String {
        val list = cookies.stream()
            .filter { it: String -> it.startsWith("token") }
            .map { it: String ->
                it.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0].split("=".toRegex())
                    .dropLastWhile { it.isEmpty() }.toTypedArray()[1]
            }
            .toList()
        return list[0]
    }

    fun findInstanceName(podUrl: String, id: String, detectTimesTmp: Int): String {
        var detectTimes = detectTimesTmp
        val resObj = HttpClientUtil.getForObjectWithToken(
            podUrl, null,
            JsonObject::class.java, project
        )

        val items = resObj.getAsJsonArray("items")
        for (item in items) {
            val itemObject = item as JsonObject
            val specObj = itemObject.getAsJsonObject("spec")
            val containers = specObj.getAsJsonArray("containers")
            for (container in containers) {
                val containerObject = container as JsonObject
                val image = containerObject["image"].asString
                if (image.endsWith(id)) {
                    return itemObject.getAsJsonObject("metadata")["name"].asString
                }
            }
        }
        detectTimes++
        if (detectTimes >= 18) {
            throw RuntimeException("当前url:$podUrl,返回结果:$resObj")
        }
        TimeUnit.SECONDS.sleep(10)

        return findInstanceName(podUrl, id, detectTimes)
    }

    fun findInstanceName(serviceName: String): List<InstanceVo> {
        val configService = ConfigService.getInstance(project)
        val podsUrl = configService.getPodsUrl(serviceName)
        val resObj = HttpClientUtil.getForObjectWithToken(
            podsUrl, null,
            JsonObject::class.java, project
        )

        val instanceVos: MutableList<InstanceVo> = ArrayList()
        val items = resObj.getAsJsonArray("items")
        for (item in items) {
            val itemObject = item as JsonObject
            val statusObj = itemObject.getAsJsonObject("status")
            val ready1 = getReady(statusObj, "initContainerStatuses")
            val ready2 = getReady(statusObj, "containerStatuses")
            val instanceName = itemObject.getAsJsonObject("metadata")["name"].asString
            val instanceVo = if (ready1 && ready2) {
                InstanceVo(instanceName, "运行中", false)
            } else {
                InstanceVo(instanceName, "未就绪", false)
            }
            instanceVos.add(instanceVo)
        }
        return instanceVos
    }

    fun getRestartCount(statusObj: JsonObject, key: String): Int {
        val statuses = statusObj.getAsJsonArray(key) ?: return 0
        var sum = 0
        for (status in statuses) {
            val tmpObj = status as JsonObject
            sum += tmpObj["restartCount"].asInt
        }
        return sum
    }

    fun getReady(statusObj: JsonObject, key: String): Boolean {
        val statuses = statusObj.getAsJsonArray(key) ?: return true
        val list: MutableList<Boolean> = ArrayList()
        for (status in statuses) {
            val tmpObj = status as JsonObject
            list.add(tmpObj["ready"].asBoolean)
        }
        return list.stream().allMatch { it: Boolean? -> it!! }
    }

    fun getBuildErrorInfo(url: String): Pair<ByteArray, ByteArray> {
        val urlPush = "$url/log/?start=0"
        val futurePush = CompletableFuture.supplyAsync {
            return@supplyAsync HttpClientUtil.getForObjectWithToken<ByteArray>(
                urlPush, null,
                ByteArray::class.java, project
            )
        }.exceptionally { e ->
            ExceptionUtils.getStackTrace(e)
                .toByteArray(StandardCharsets.UTF_8)
        }

        val configService = ConfigService.getInstance(project)
        val compileLogPath = configService.getCompileLogPath()
        if (StringUtils.isBlank(compileLogPath)) {
            throw RuntimeException("构建失败了,由于未配置compileLogPath参数,无法获取详细构建错误信息")
        }

        val urlCompile = url + compileLogPath
        val futureCompile = CompletableFuture.supplyAsync {
            try {
                return@supplyAsync HttpClientUtil.getForObjectWithToken<ByteArray>(
                    urlCompile, null,
                    ByteArray::class.java, project
                )
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }.exceptionally { e: Throwable? ->
            ExceptionUtils.getStackTrace(e)
                .toByteArray(StandardCharsets.UTF_8)
        }

        CompletableFuture.allOf(futurePush, futureCompile).join()
        try {
            return Pair.create(futurePush.get(), futureCompile.get())
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    fun getContainerStartInfo(
        selectService: String, newInstanceName: String?, tailLines: Int, previous: Boolean, follow: Boolean,
    ): ByteArray {
        val configService = ConfigService.getInstance(project)
        val logsUrl = configService.getLogsUrl(selectService, newInstanceName, tailLines, previous, follow)
        return HttpClientUtil.getForObjectWithTokenUseUrl(
            logsUrl, null,
            ByteArray::class.java, project
        )
    }

    fun getContainerStartInfo(
        selectService: String, newInstanceName: String?, tailLines: Int, previous: Boolean,
        follow: Boolean, consumer: Consumer<ByteArray>?,
    ) {
        val configService = ConfigService.getInstance(project)
        val logsUrl = configService.getLogsUrl(selectService, newInstanceName, tailLines, previous, follow)
        HttpClientUtil.getForObjectWithTokenUseUrl(
            logsUrl, null,
            ByteArray::class.java, consumer, project
        )
    }

    private fun encrypt(@Suppress("SameParameterValue") e1: String, t1: String): String {
        var e = e1
        var t = t1
        t = Base64.getEncoder().encodeToString(t.toByteArray())
        if (t.length > e.length) {
            e += t.substring(0, t.length - e.length)
        }
        val binary = StringBuilder()
        val chars = StringBuilder()
        for (i in e.indices) {
            val k = e[i].code
            val p = (if (i < t.length) t[i] else 64.toChar()).code
            val sum = k + p
            binary.append(if (sum % 2 == 0) "0" else "1")
            chars.append((sum / 2).toChar())
        }
        return Base64.getEncoder().encodeToString(binary.toString().toByteArray()) + "@" + chars
    }

    companion object {

        fun getInstance(project: Project): KubesphereService {
            return project.getService(KubesphereService::class.java)
        }

    }
}
