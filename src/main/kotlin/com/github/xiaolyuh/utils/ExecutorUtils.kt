package com.github.xiaolyuh.utils

import com.github.xiaolyuh.service.ConfigService.Companion.getInstance
import com.github.xiaolyuh.service.RunTask
import com.github.xiaolyuh.ui.KbsMsgForm
import com.google.gson.JsonObject
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.util.application
import org.apache.commons.lang3.exception.ExceptionUtils
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

object ExecutorUtils {
    private val LOG = Logger.getInstance(ExecutorUtils::class.java)

    fun addTask(runnable: RunTask): Future<*> {
        return application.executeOnPooledThread(runnable)
    }

    fun monitorBuildTask(id: String, selectService: String, project: Project) {
        addTask {
            sleep(30)
            createMonitorBuildTask(id, selectService, project)
        }
    }

    private fun createMonitorBuildTask(id: String, selectService: String, project: Project) {
        val task = createMonitorBuildTaskReal(id, selectService, project)

        addTask(task)
    }

    private fun createMonitorBuildTaskReal(id: String, selectService: String, project: Project): RunTask {
        return RunTask {
            try {
                val configService = getInstance(project)

                val url = configService.getRunsUrl() + "/" + id
                val resObj = HttpClientUtil.getForObjectWithToken(
                    "$url/", null,
                    JsonObject::class.java, project
                )

                val state = resObj["state"].asString
                if ("FINISHED" != state) {
                    sleep(10)

                    // 构建未完成,重新监控
                    createMonitorBuildTask(id, selectService, project)
                    return@RunTask
                }

                val result = resObj["result"].asString
                if ("SUCCESS" != result) {
                    val title = selectService + " id为" + id + "构建失败"
                    NotifyUtil.notifyInfo(project, title)

                    val pair = KubesphereUtils.getBuildErrorInfo(url, project)

                    runInEdt {
                        val dialog = KbsMsgForm(pair, project)
                        dialog.isVisible = true
                    }

                    return@RunTask
                }

                NotifyUtil.notifySuccess(project, selectService + " id为" + id + "构建成功")

                monitorStartTask(selectService, id, project)

                NotifyUtil.notifyInfo(project, "开始监控" + selectService + " id为" + id + "启动情况")
            } catch (e: Exception) {
                LOG.warn(e)
                NotifyUtil.notifyError(
                    project,
                    "检测" + selectService + " id为" + id + "的构建情况出错,原因:" + ExceptionUtils.getStackTrace(e)
                )
            }
        }
    }

    private fun monitorStartTask(selectService: String, id: String, project: Project) {
        addTask {
            sleep(10)
            val configService = getInstance(project)
            val podUrl = configService.getPodsUrl(selectService)

            val newInstanceName: String
            try {
                newInstanceName = KubesphereUtils.findInstanceName(podUrl, id, 0, project)
            } catch (e: Exception) {
                NotifyUtil.notifyError(
                    project, "检测" + selectService + " id为" + id +
                            "的启动情况出错啦,原因:" + ExceptionUtils.getStackTrace(e)
                )
                return@addTask
            }

            NotifyUtil.notifyInfo(project, "新实例" + newInstanceName + "启动中......")

            sleep(10)
            monitorStartedTask(podUrl, selectService, project, newInstanceName)
        }
    }

    private fun monitorStartedTask(
        podUrl: String, selectService: String, project: Project,
        newInstanceName: String,
    ) {
        val task = createMonitorStartTask(podUrl, selectService, project, newInstanceName)

        addTask(task)
    }

    private fun createMonitorStartTask(
        podUrl: String, selectService: String, project: Project,
        newInstanceName: String,
    ): RunTask {
        return RunTask {
            try {
                val resObj = HttpClientUtil.getForObjectWithToken(
                    podUrl, null,
                    JsonObject::class.java, project
                )
                val items = resObj.getAsJsonArray("items")

                val list: MutableList<JsonObject> = ArrayList()
                for (item in items) {
                    val itemObject = item as JsonObject
                    val instanceName = itemObject.getAsJsonObject("metadata")["name"].asString
                    if (instanceName == newInstanceName) {
                        list.add(itemObject)
                        break
                    }
                }

                if (list.isEmpty()) {
                    NotifyUtil.notifyError(
                        project,
                        newInstanceName + "实例已不存在,直接开始监控实例数量,当前数量:" + items.size()
                    )

                    monitorServiceNumTask(podUrl, selectService, project)

                    return@RunTask
                }

                val newItemObject = list[0]

                checkNewInstance(newItemObject, newInstanceName, project, selectService, podUrl)
            } catch (e: Exception) {
                NotifyUtil.notifyError(
                    project,
                    "检测" + newInstanceName + "启动情况出错,原因:" + ExceptionUtils.getStackTrace(e)
                )
            }
        }
    }

    private fun checkNewInstance(
        newItemObject: JsonObject, newInstanceName: String, project: Project,
        selectService: String, podUrl: String,
    ) {
        val statusObj = newItemObject.getAsJsonObject("status")
        var restartCount = KubesphereUtils.getRestartCount(statusObj, "initContainerStatuses")
        if (restartCount > 0) {
            sleep(30)

            val title = newInstanceName + "容器初始化失败,当前重启次数:" + restartCount
            NotifyUtil.notifyInfo(project, title)

            val errorBytes = KubesphereUtils.getContainerStartInfo(
                project, selectService, newInstanceName,
                500, false, false
            )

            runInEdt {
                val dialog = KbsMsgForm(
                    errorBytes, project, selectService,
                    newInstanceName, false
                )
                dialog.isVisible = true
            }

            return
        }

        restartCount = KubesphereUtils.getRestartCount(statusObj, "containerStatuses")
        if (restartCount > 0) {
            sleep(30)

            val title = newInstanceName + "容器启动失败,当前重启次数:" + restartCount
            NotifyUtil.notifyInfo(project, title)

            val errorBytes = KubesphereUtils.getContainerStartInfo(
                project, selectService, newInstanceName,
                500, false, false
            )

            runInEdt {
                val dialog = KbsMsgForm(
                    errorBytes, project, selectService,
                    newInstanceName, false
                )
                dialog.isVisible = true
            }

            return
        }

        var ready = KubesphereUtils.getReady(statusObj, "initContainerStatuses")
        if (!ready) {
            sleep(10)

            monitorStartedTask(podUrl, selectService, project, newInstanceName)

            return
        }

        ready = KubesphereUtils.getReady(statusObj, "containerStatuses")
        if (!ready) {
            sleep(10)

            monitorStartedTask(podUrl, selectService, project, newInstanceName)

            return
        }

        NotifyUtil.notifySuccess(project, newInstanceName + "新实例已启动成功,开始监控实例数量")

        monitorServiceNumTask(podUrl, selectService, project)
    }

    private fun monitorServiceNumTask(podUrl: String, selectService: String, project: Project?) {
        val task = createMonitorServiceNumTask(podUrl, selectService, project)

        addTask(task)
    }

    private fun createMonitorServiceNumTask(podUrl: String, selectService: String, project: Project?): RunTask {
        return RunTask {
            try {
                val resObj = HttpClientUtil.getForObjectWithToken(
                    podUrl, null,
                    JsonObject::class.java, project
                )
                val items = resObj.getAsJsonArray("items")
                if (items.size() > 1) {
                    sleep(10)

                    monitorServiceNumTask(podUrl, selectService, project)

                    return@RunTask
                }

                NotifyUtil.notifySuccess(project, selectService + "新实例已完全替换成功!")
            } catch (e: Exception) {
                NotifyUtil.notifyError(
                    project,
                    "检测" + selectService + "服务实例数量出错,原因:" + ExceptionUtils.getStackTrace(e)
                )
            }
        }
    }

    private fun sleep(seconds: Int) {
        TimeUnit.SECONDS.sleep(seconds.toLong())
    }
}
