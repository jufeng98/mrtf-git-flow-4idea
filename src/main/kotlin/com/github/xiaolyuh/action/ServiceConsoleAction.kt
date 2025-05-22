package com.github.xiaolyuh.action

import com.github.xiaolyuh.i18n.I18n
import com.github.xiaolyuh.service.ConfigService.Companion.getInstance
import com.github.xiaolyuh.ui.JcefK8sConsoleDialog
import com.github.xiaolyuh.ui.ServiceDialog
import com.github.xiaolyuh.utils.KubesphereUtils
import com.github.xiaolyuh.utils.NotifyUtil
import com.github.xiaolyuh.utils.StringUtils
import com.github.xiaolyuh.vo.InstanceVo
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import org.apache.commons.lang3.exception.ExceptionUtils

class ServiceConsoleAction : AnAction(), DumbAware {
    
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        val serviceDialog = ServiceDialog(I18n.getContent("choose.console"), project)
        serviceDialog.selectLastChoose()
        if (!serviceDialog.showAndGet()) {
            return
        }

        val selectService = serviceDialog.selectService
        if (StringUtils.isBlank(selectService)) {
            return
        }

        val configService = getInstance(project!!)

        if (configService.notExistsK8sOptions()) {
            NotifyUtil.notifyError(project, I18n.getContent("lack.k8s.config"))
            return
        }

        object : Task.Backgroundable(project, selectService + "获取信息中...", true) {
            override fun run(indicator: ProgressIndicator) {
                val instanceVos: List<InstanceVo>
                try {
                    instanceVos = KubesphereUtils.findInstanceName(project, selectService)
                } catch (e: Exception) {
                    NotifyUtil.notifyError(project, ExceptionUtils.getStackTrace(e))
                    return
                }

                if (instanceVos.isEmpty()) {
                    NotifyUtil.notifyError(project, I18n.getContent("no.service.instances"))
                    return
                }

                runInEdt {
                    showInstances(project, instanceVos, selectService)
                }
            }
        }.queue()
    }

    fun showInstances(project: Project?, instanceVos: List<InstanceVo>, selectService: String?) {
        var choose = 0
        if (instanceVos.size > 1) {
            val options = instanceVos.stream()
                .map { it.desc + ":" + it.name }
                .toList()
                .toTypedArray()

            choose = Messages.showDialog(
                project, I18n.getContent("multi.service.instances"), "温馨提示",
                options, 0, null
            )
        }

        if (choose == -1) {
            return
        }

        val instanceVo = instanceVos[choose]

        showInstanceDialog(project, instanceVo, selectService)
    }

    private fun showInstanceDialog(project: Project?, instanceVo: InstanceVo, selectService: String?) {
        try {
            JcefK8sConsoleDialog(instanceVo, project, selectService)
        } catch (e: Exception) {
            NotifyUtil.notifyError(project, ExceptionUtils.getStackTrace(e))
        }
    }
}
