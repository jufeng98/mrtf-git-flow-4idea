package com.github.xiaolyuh.action

import com.github.xiaolyuh.i18n.I18n
import com.github.xiaolyuh.i18n.I18nKey
import com.github.xiaolyuh.icons.GitFlowPlusIcons.start
import com.github.xiaolyuh.service.ConfigService
import com.github.xiaolyuh.service.ConfigService.Companion.getInstance
import com.github.xiaolyuh.service.GitFlowPlus
import com.github.xiaolyuh.utils.ActionUtils
import com.github.xiaolyuh.utils.ActionUtils.shouldShow
import com.github.xiaolyuh.valve.merge.ChangeFileValve
import com.github.xiaolyuh.valve.merge.MergeValve
import com.github.xiaolyuh.valve.merge.Valve
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project

/**
 * 预发布
 */
@Suppress("ActionPresentationInstantiatedInCtor")
class StagingAction : AbstractMergeAction(I18n.nls("action.staging.txt"), I18n.nls("action.staging.desc"), start) {
    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = ActionUtils.shouldShowStaging(e)
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    override fun setEnabledAndText(event: AnActionEvent) {
        event.presentation.setText(I18n.getContent("StartStagingAction.text"))
    }

    override fun getTargetBranch(project: Project): String? {
        val configService = getInstance(project)
        return configService.getInitOptions().stagingBranch
    }

    override fun getDialogTitle(project: Project?): String {
        return I18n.getContent("StartStagingAction.text")
    }

    override fun getTaskTitle(project: Project): String {
        return I18n.getContent(
            I18nKey.MERGE_BRANCH_TASK_TITLE,
            GitFlowPlus.getInstance().getCurrentBranch(project), getTargetBranch(project)
        )
    }

    override fun getValves(): MutableList<Valve?> {
        val valves: MutableList<Valve?> = ArrayList<Valve?>()
        valves.add(ChangeFileValve.getInstance())
        valves.add(MergeValve.getInstance())
        return valves
    }
}
