package com.github.xiaolyuh.service

import com.github.xiaolyuh.action.ServiceLogAction.Companion.showLogInRunToolWindow
import com.github.xiaolyuh.ui.KbsMsgForm
import com.github.xiaolyuh.utils.NotifyUtil
import com.google.gson.JsonObject
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.util.application
import org.apache.commons.lang3.exception.ExceptionUtils
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

/**
 * @author yudong
 */
@Service(Service.Level.PROJECT)
class ExecutorService(private val project: Project) {
    private val logger = Logger.getInstance(ExecutorService::class.java)

    fun addTask(runnable: RunTask): Future<*> {
        return application.executeOnPooledThread(runnable)
    }

    fun txt(mainTest: Boolean): String {
        return if (mainTest) {
            ""
        } else {
            "(Sec)"
        }
    }

    fun monitorBuildTask(id: String, selectService: String, mainTest: Boolean) {
        addTask {
            sleep(30)

            createMonitorBuildTask(id, selectService, mainTest)
        }
    }

    private fun createMonitorBuildTask(id: String, selectService: String, mainTest: Boolean) {
        val task = createMonitorBuildTaskReal(id, selectService, mainTest)

        addTask(task)
    }

    private fun createMonitorBuildTaskReal(id: String, selectService: String, mainTest: Boolean): RunTask {
        return RunTask {
            try {
                val configService = ConfigService.getInstance(project)

                val url = configService.getRunsUrl(mainTest) + "/" + id

                val httpClientService = HttpClientService.getInstance(project)

                val resObj = httpClientService.getForObjectWithToken(
                    "$url/", null,
                    JsonObject::class.java
                )

                val state = resObj["state"].asString
                if ("FINISHED" != state) {
                    sleep(10)

                    // 构建未完成,重新监控
                    createMonitorBuildTask(id, selectService, mainTest)
                    return@RunTask
                }

                val result = resObj["result"].asString
                if ("SUCCESS" != result) {
                    val title = selectService + " id为" + id + "构建失败" + txt(mainTest)
                    NotifyUtil.notifyError(project, title)

                    val kubesphereService = KubesphereService.getInstance(project)
                    val pair = kubesphereService.getBuildErrorInfo(id, mainTest)

                    runInEdt {
                        val form = KbsMsgForm(pair, project, mainTest)

                        showLogInRunToolWindow(form, project, "$selectService-built-error")
                    }

                    return@RunTask
                }

                NotifyUtil.notifySuccess(
                    project, selectService + " id为" + id + "构建成功" + txt(mainTest)
                )

                monitorStartTask(selectService, id, mainTest)

                NotifyUtil.notifyInfo(
                    project, "开始监控" + selectService + " id为" + id + "启动情况" + txt(mainTest)
                )
            } catch (e: Exception) {
                logger.warn(e)

                NotifyUtil.notifyError(
                    project,
                    txt(mainTest) + "检测" + selectService + " id为" + id + "的构建情况出错,原因:" + ExceptionUtils.getStackTrace(
                        e
                    )
                )
            }
        }
    }

    private fun monitorStartTask(selectService: String, id: String, mainTest: Boolean) {
        addTask {
            sleep(10)

            val configService = ConfigService.getInstance(project)

            val podUrl = configService.getPodsUrl(selectService, mainTest)

            val newInstanceName: String
            try {
                val kubesphereService = KubesphereService.getInstance(project)
                newInstanceName = kubesphereService.findInstanceName(podUrl, id, 0)
            } catch (e: Exception) {
                e.printStackTrace()

                NotifyUtil.notifyError(
                    project,
                    txt(mainTest) + "检测" + selectService + " id为" + id + "的启动情况出错啦,原因:" + ExceptionUtils.getStackTrace(
                        e
                    )
                )
                return@addTask
            }

            NotifyUtil.notifyInfo(
                project, "新实例" + newInstanceName + "启动中......" + txt(mainTest)
            )

            sleep(10)

            monitorStartedTask(podUrl, selectService, newInstanceName, mainTest)
        }
    }

    private fun monitorStartedTask(
        podUrl: String, selectService: String, newInstanceName: String, mainTest: Boolean,
    ) {
        val task = createMonitorStartTask(podUrl, selectService, newInstanceName, mainTest)

        addTask(task)
    }

    private fun createMonitorStartTask(
        podUrl: String, selectService: String, newInstanceName: String, mainTest: Boolean,
    ): RunTask {
        return RunTask {
            try {
                val httpClientService = HttpClientService.getInstance(project)

                val resObj = httpClientService.getForObjectWithToken(
                    podUrl, null,
                    JsonObject::class.java
                )

                val items = resObj.getAsJsonArray("items")

                val list = mutableListOf<JsonObject>()
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
                        newInstanceName + "实例已不存在,直接开始监控实例数量,当前数量:" + items.size() + txt(mainTest)
                    )

                    monitorServiceNumTask(podUrl, selectService, mainTest)

                    return@RunTask
                }

                val newItemObject = list[0]

                checkNewInstance(newItemObject, newInstanceName, selectService, podUrl, mainTest)
            } catch (e: Exception) {
                e.printStackTrace()

                NotifyUtil.notifyError(
                    project,
                    txt(mainTest) + "检测" + newInstanceName + "启动情况出错,原因:" + ExceptionUtils.getStackTrace(e)
                )
            }
        }
    }

    private fun checkNewInstance(
        newItemObject: JsonObject, newInstanceName: String, selectService: String,
        podUrl: String, mainTest: Boolean,
    ) {
        val statusObj = newItemObject.getAsJsonObject("status")
        val kubesphereService = KubesphereService.getInstance(project)
        var restartCount = kubesphereService.getRestartCount(statusObj, "initContainerStatuses")
        if (restartCount > 0) {
            sleep(30)

            val title = newInstanceName + "容器初始化失败,当前重启次数:" + restartCount + txt(mainTest)
            NotifyUtil.notifyError(project, title)

            val errorBytes = kubesphereService.getContainerStartInfo(
                selectService, newInstanceName,
                500, previous = false, follow = false, mainTest
            )

            runInEdt {
                val form = KbsMsgForm(
                    errorBytes, project, selectService,
                    newInstanceName, false, mainTest
                )

                showLogInRunToolWindow(form, project, "$selectService-initialed-error")
            }

            return
        }

        restartCount = kubesphereService.getRestartCount(statusObj, "containerStatuses")
        if (restartCount > 0) {
            sleep(30)

            val title = newInstanceName + "容器启动失败,当前重启次数:" + restartCount + txt(mainTest)
            NotifyUtil.notifyError(project, title)

            val errorBytes = kubesphereService.getContainerStartInfo(
                selectService, newInstanceName,
                500, previous = false, follow = false, mainTest
            )

            runInEdt {
                val form = KbsMsgForm(
                    errorBytes, project, selectService,
                    newInstanceName, false, mainTest
                )

                showLogInRunToolWindow(form, project, "$selectService-started-error")
            }

            return
        }

        var ready = kubesphereService.getReady(statusObj, "initContainerStatuses")
        if (!ready) {
            sleep(10)

            monitorStartedTask(podUrl, selectService, newInstanceName, mainTest)

            return
        }

        ready = kubesphereService.getReady(statusObj, "containerStatuses")
        if (!ready) {
            sleep(10)

            monitorStartedTask(podUrl, selectService, newInstanceName, mainTest)

            return
        }

        NotifyUtil.notifySuccess(
            project, newInstanceName + "新实例已启动成功,开始监控实例数量" + txt(mainTest)
        )

        monitorServiceNumTask(podUrl, selectService, mainTest)
    }

    private fun monitorServiceNumTask(podUrl: String, selectService: String, mainTest: Boolean) {
        val task = createMonitorServiceNumTask(podUrl, selectService, mainTest)

        addTask(task)
    }

    private fun createMonitorServiceNumTask(podUrl: String, selectService: String, mainTest: Boolean): RunTask {
        return RunTask {
            try {
                val httpClientService = HttpClientService.getInstance(project)
                val resObj = httpClientService.getForObjectWithToken(
                    podUrl, null,
                    JsonObject::class.java
                )
                val items = resObj.getAsJsonArray("items")
                if (items.size() > 1) {
                    sleep(10)

                    monitorServiceNumTask(podUrl, selectService, mainTest)

                    return@RunTask
                }

                NotifyUtil.notifySuccess(
                    project, selectService + "新实例已完全替换成功!" + txt(mainTest)
                )
            } catch (e: Exception) {
                e.printStackTrace()

                NotifyUtil.notifyError(
                    project,
                    txt(mainTest) + "检测" + selectService + "服务实例数量出错,原因:" + ExceptionUtils.getStackTrace(e)
                )
            }
        }
    }

    private fun sleep(seconds: Int) {
        TimeUnit.SECONDS.sleep(seconds.toLong())
    }

    companion object {
        fun getInstance(project: Project): ExecutorService {
            return project.getService(ExecutorService::class.java)
        }
    }
}
