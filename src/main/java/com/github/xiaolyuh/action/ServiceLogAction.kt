package com.github.xiaolyuh.action

import com.github.xiaolyuh.ui.KbsMsgDialog
import com.github.xiaolyuh.ui.ServiceDialog
import com.github.xiaolyuh.utils.ConfigUtil
import com.github.xiaolyuh.utils.KubesphereUtils
import com.github.xiaolyuh.utils.NotifyUtil
import com.github.xiaolyuh.utils.StringUtils
import com.github.xiaolyuh.vo.InstanceVo
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.apache.commons.lang3.exception.ExceptionUtils
import java.util.stream.Collectors

class ServiceLogAction : AnAction(), DumbAware {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        val serviceDialog = ServiceDialog("选择需要查看的服务", project)
        serviceDialog.selectLastChoose()
        if (!serviceDialog.showAndGet()) {
            return
        }

        if (ConfigUtil.notExistsK8sOptions(project)) {
            NotifyUtil.notifyError(project, "缺少k8s配置")
            return
        }

        val selectService = serviceDialog.selectService
        if (StringUtils.isBlank(selectService)) {
            return
        }

        object : Task.Backgroundable(project, "正在获取${selectService}服务实例...", true) {
            override fun run(indicator: ProgressIndicator) {
                val instanceVos: List<InstanceVo>
                try {
                    instanceVos = KubesphereUtils.findInstanceName(project, selectService)
                } catch (e: Exception) {
                    NotifyUtil.notifyError(project, ExceptionUtils.getStackTrace(e))
                    return
                }

                if (instanceVos.isEmpty()) {
                    NotifyUtil.notifyError(project, "没有任何服务实例!")
                    return
                }

                if (instanceVos.size == 1) {
                    val instanceVo = instanceVos[0]
                    getInstanceLogAndShow(project, selectService, instanceVo)
                    return
                }

                CoroutineScope(Dispatchers.Main).launch {
                    val instanceChooseIdx = showAndGetInstanceChooseDialog(project, instanceVos)
                    if (instanceChooseIdx == -1) {
                        return@launch
                    }

                    val instanceVo = instanceVos[instanceChooseIdx]
                    getInstanceLogAndShow(project, selectService, instanceVo)
                }
            }
        }.queue()
    }

    fun showAndGetInstanceChooseDialog(project: Project?, instanceVos: List<InstanceVo>): Int {
        val options = instanceVos.stream()
            .map { instanceVo: InstanceVo -> instanceVo.desc + ":" + instanceVo.name }
            .collect(Collectors.toList())
            .toTypedArray()


        @Suppress("DialogTitleCapitalization")
        return Messages.showDialog(
            project, "找到多个服务实例,请选择:", "温馨提示",
            options, 0, null
        )
    }

    fun getInstanceLogAndShow(project: Project?, selectService: String?, instanceVo: InstanceVo) {
        object : Task.Backgroundable(project, "正在获取${instanceVo.name}日志...", true) {
            override fun run(indicator: ProgressIndicator) {
                val textBytes: ByteArray
                try {
                    textBytes = KubesphereUtils.getContainerStartInfo(
                        project, selectService, instanceVo.name,
                        500, instanceVo.isPreviews, false
                    )
                } catch (e: Exception) {
                    NotifyUtil.notifyError(project, ExceptionUtils.getStackTrace(e))
                    return
                }

                CoroutineScope(Dispatchers.Main).launch {
                    val title = instanceVo.desc + ":" + instanceVo.name
                    val dialog =
                        KbsMsgDialog(title, textBytes, project, selectService, instanceVo.name, false)
                    dialog.show()
                }
            }
        }.queue()
    }
}