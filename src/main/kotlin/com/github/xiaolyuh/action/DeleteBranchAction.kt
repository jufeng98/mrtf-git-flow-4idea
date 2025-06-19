package com.github.xiaolyuh.action

import com.github.xiaolyuh.i18n.I18n
import com.github.xiaolyuh.icons.GitFlowPlusIcons
import com.github.xiaolyuh.service.ConfigService.Companion.getInstance
import com.github.xiaolyuh.ui.BranchDeleteDialog
import com.github.xiaolyuh.utils.GitBranchUtil
import com.github.xiaolyuh.utils.NotifyUtil
import com.github.xiaolyuh.valve.merge.ChangeFileValve
import com.github.xiaolyuh.valve.merge.Valve
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VirtualFileManager
import git4idea.repo.GitRepository

/**
 * @author yudong
 */
@Suppress("ActionPresentationInstantiatedInCtor")
class DeleteBranchAction :
    AbstractMergeAction(I18n.nls("action.delete.txt"), I18n.nls("action.delete.desc"), GitFlowPlusIcons.deleteTag) {

    override fun update(event: AnActionEvent) {
        event.presentation.isEnabled = GitBranchUtil.isGitProject(event.project)
    }

    override fun setEnabledAndText(event: AnActionEvent) {
    }

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project!!
        val repository = GitBranchUtil.getCurrentRepository(project)
        if (gitFlowPlus.isExistChangeFile(project)) {
            return
        }

        val branchDeleteDialog = BranchDeleteDialog(repository)
        if (!branchDeleteDialog.showAndGet()) {
            return
        }

        val flag = Messages.showOkCancelDialog(
            project,
            I18n.getContent("DeleteBranchAction.confirm"), I18n.getContent("DeleteBranchAction.text"),
            I18n.getContent("OkText"), I18n.getContent("CancelText"),
            GitFlowPlusIcons.warning
        )
        if (flag != 0) {
            return
        }

        val deleteBranchOptions = branchDeleteDialog.deleteBranchOptions

        val tip = I18n.getContent("deleting.branches", deleteBranchOptions.branches.size)
        object : Task.Backgroundable(project, tip, false) {

            override fun run(indicator: ProgressIndicator) {
                var i = 1
                val faction = 1.0 / deleteBranchOptions.branches.size

                NotifyUtil.notifyGitCommand(event.project, "=====================================")
                val currentBranchName = repository.currentBranch!!.name

                val map = deleteBranchOptions.branches.groupBy { it.branch == currentBranchName }

                map.getOrDefault(false, emptyList())
                    .forEach {
                        gitFlowPlus.deleteBranch(repository, it.branch, deleteBranchOptions.isDeleteLocalBranch)

                        indicator.fraction = faction * i++
                    }

                val branchVos = map[true]
                if (branchVos != null) {
                    val branchVo = branchVos[0]

                    gitFlowPlus.deleteBranch(repository, "master", branchVo.branch)

                    indicator.fraction = faction * i++
                }

                repository.update()

                project.messageBus.syncPublisher(GitRepository.GIT_REPO_CHANGE).repositoryChanged(repository)

                VirtualFileManager.getInstance().asyncRefresh(null)

                val message = I18n.getContent("deleting.branches.finished", deleteBranchOptions.branches.size)
                NotifyUtil.notifySuccess(event.project, message)
            }
        }.queue()
    }

    override fun getTargetBranch(project: Project): String {
        val configService = getInstance(project)

        return configService.getInitOptions().masterBranch
    }

    override fun getDialogTitle(project: Project): String {
        return ""
    }

    override fun getTaskTitle(project: Project): String {
        return ""
    }

    override fun getValves(): List<Valve> {
        val valves = mutableListOf<Valve>()

        valves.add(ChangeFileValve.getInstance())

        return valves
    }
}
