package com.github.xiaolyuh.action

import com.github.xiaolyuh.i18n.I18n
import com.github.xiaolyuh.icons.GitFlowPlusIcons
import com.github.xiaolyuh.provider.ConsoleVirtualFile
import com.github.xiaolyuh.service.KubesphereService
import com.github.xiaolyuh.ui.JcefK8sConsoleForm
import com.github.xiaolyuh.ui.ServiceDialog
import com.github.xiaolyuh.utils.ActionUtils
import com.github.xiaolyuh.utils.NotifyUtil
import com.github.xiaolyuh.utils.StringUtils
import com.github.xiaolyuh.vo.InstanceVo
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import org.apache.commons.lang3.exception.ExceptionUtils

/**
 * @author yudong
 */
@Suppress("ActionPresentationInstantiatedInCtor")
class ServiceConsoleAction :
    AnAction(I18n.nls("action.console.txt"), I18n.nls("action.console.desc"), GitFlowPlusIcons.show),
    DumbAware {
    private var jcefInitialed = false

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = ActionUtils.shouldShow(e)
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.EDT
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project!!

        val serviceDialog = ServiceDialog(I18n.getContent("choose.console"), project)

        serviceDialog.selectLastChoose()

        if (!serviceDialog.showAndGet()) {
            return
        }

        val selectService = serviceDialog.selectService
        if (StringUtils.isBlank(selectService)) {
            return
        }

        object : Task.Backgroundable(project, selectService + "获取信息中...", true) {
            override fun run(indicator: ProgressIndicator) {
                val instanceVos: List<InstanceVo>
                try {
                    val kubesphereService = KubesphereService.getInstance(project)
                    instanceVos = kubesphereService.findInstanceName(selectService, true)
                } catch (e: Exception) {
                    e.printStackTrace()

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

    fun showInstances(project: Project, instanceVos: List<InstanceVo>, selectService: String) {
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

        if (!jcefInitialed) {
            val form = JcefK8sConsoleForm(instanceVo, project, selectService, true)

            form.dispose()

            jcefInitialed = true
            return
        }

        val fileEditorManager = FileEditorManager.getInstance(project)
        fileEditorManager.openFile(
            ConsoleVirtualFile(
                "${selectService}-remote-console",
                selectService,
                instanceVo,
                project,
                true
            )
        )
    }

}
