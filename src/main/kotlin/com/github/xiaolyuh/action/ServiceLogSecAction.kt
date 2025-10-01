package com.github.xiaolyuh.action

import com.github.xiaolyuh.i18n.I18n
import com.github.xiaolyuh.icons.GitFlowPlusIcons
import com.github.xiaolyuh.service.KubesphereService
import com.github.xiaolyuh.ui.KbsMsgForm
import com.github.xiaolyuh.ui.ServiceDialog
import com.github.xiaolyuh.utils.ActionUtils
import com.github.xiaolyuh.utils.NotifyUtil
import com.github.xiaolyuh.utils.StringUtils
import com.github.xiaolyuh.vo.InstanceVo
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.ui.RunContentDescriptor
import com.intellij.execution.ui.RunContentManager
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.ToolWindowId
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.ui.content.ContentFactory
import org.apache.commons.lang3.exception.ExceptionUtils


/**
 * @author yudong
 */
@Suppress("ActionPresentationInstantiatedInCtor")
class ServiceLogSecAction :
    AnAction(I18n.nls("action.log.txt") + "(Sec)", I18n.nls("action.log.desc") + "(Sec)", GitFlowPlusIcons.show),
    DumbAware {

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = ActionUtils.shouldShowSec(e)
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.EDT
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project!!

        val serviceDialog = ServiceDialog(I18n.getContent("choose.service"), project)

        serviceDialog.selectLastChoose()

        if (!serviceDialog.showAndGet()) {
            return
        }

        val selectService = serviceDialog.selectService
        if (StringUtils.isBlank(selectService)) {
            return
        }

        object : Task.Backgroundable(project, "正在获取 $selectService 服务实例...", true) {

            override fun run(indicator: ProgressIndicator) {
                val instanceVos: List<InstanceVo>

                try {
                    val kubesphereService = KubesphereService.getInstance(project)
                    instanceVos = kubesphereService.findInstanceName(selectService, false)
                } catch (e: Exception) {
                    e.printStackTrace()

                    NotifyUtil.notifyError(project, ExceptionUtils.getStackTrace(e))
                    return
                }

                if (instanceVos.isEmpty()) {
                    NotifyUtil.notifyError(project, I18n.getContent("no.service.instances"))
                    return
                }

                if (instanceVos.size == 1) {
                    val instanceVo = instanceVos[0]

                    fetchInstanceLogAndShow(project, selectService, instanceVo)
                    return
                }

                runInEdt {
                    val instanceChooseIdx = showAndGetInstanceChooseDialog(project, instanceVos)
                    if (instanceChooseIdx == -1) {
                        return@runInEdt
                    }

                    val instanceVo = instanceVos[instanceChooseIdx]

                    fetchInstanceLogAndShow(project, selectService, instanceVo)
                }
            }
        }.queue()
    }

    private fun showAndGetInstanceChooseDialog(project: Project?, instanceVos: List<InstanceVo>): Int {
        val options = instanceVos.stream()
            .map { it.desc + ":" + it.name }
            .toList()
            .toTypedArray()

        return Messages.showDialog(
            project, I18n.getContent("multi.service.instances"), "温馨提示",
            options, 0, null
        )
    }

    private fun fetchInstanceLogAndShow(project: Project, selectService: String, instanceVo: InstanceVo) {

        object : Task.Backgroundable(project, "正在获取 ${instanceVo.name} 日志...", true) {

            override fun run(indicator: ProgressIndicator) {
                val textBytes: ByteArray
                try {
                    val kubesphereService = KubesphereService.getInstance(project)
                    textBytes = kubesphereService.getContainerStartInfo(
                        selectService, instanceVo.name,
                        500, instanceVo.isPreviews, false, false
                    )
                } catch (e: Exception) {
                    e.printStackTrace()

                    NotifyUtil.notifyError(project, ExceptionUtils.getStackTrace(e))
                    return
                }

                runInEdt {
                    val form = KbsMsgForm(textBytes, project, selectService, instanceVo.name, false, false)

                    showLogInRunToolWindow(form, project, selectService)
                }
            }

        }.queue()
    }

    companion object {

        fun showLogInRunToolWindow(form: KbsMsgForm, project: Project, tabName: String) {
            val toolWindow = ToolWindowManager.getInstance(project).getToolWindow(ToolWindowId.RUN)
            val displayName = "$tabName-remote(Sec)"

            val executor = DefaultRunExecutor.getRunExecutorInstance()

            if (toolWindow == null) {
                val descriptor = RunContentDescriptor(form.consoleView, null, form.mainPanel, displayName)
                descriptor.isActivateToolWindowWhenAdded = true
                descriptor.isAutoFocusContent = true

                Disposer.register(descriptor, form)

                val runContentManager = RunContentManager.getInstance(project)

                runContentManager.showRunContent(executor, descriptor)
            } else {
                val contentFactory = ContentFactory.getInstance()
                val contentManager = toolWindow.contentManager

                val content = contentFactory.createContent(form.mainPanel, displayName, true)

                content.setDisposer(form)

                contentManager.addContent(content)

                contentManager.setSelectedContent(content)

                toolWindow.activate {
                    form.scrollToBottom()
                }
            }

        }
    }
}
