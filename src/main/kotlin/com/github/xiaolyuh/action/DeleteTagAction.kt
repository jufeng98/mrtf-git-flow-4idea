package com.github.xiaolyuh.action

import com.github.xiaolyuh.i18n.I18n
import com.github.xiaolyuh.icons.GitFlowPlusIcons
import com.github.xiaolyuh.service.ConfigService.Companion.getInstance
import com.github.xiaolyuh.service.GitBranchService
import com.github.xiaolyuh.ui.TagDeleteDialog
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
class DeleteTagAction :
    AbstractMergeAction(
        I18n.nls("action.delete.tag.txt"),
        I18n.nls("action.delete.tag.txt"),
        GitFlowPlusIcons.deleteBlack
    ) {

    override fun update(event: AnActionEvent) {
        event.presentation.isEnabled = GitBranchService.isGitProject(event.project)
    }

    override fun setEnabledAndText(event: AnActionEvent) {
    }

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return

        if (gitFlowPlus.isExistChangeFile(project)) {
            return
        }

        val repository = GitBranchService.getCurrentRepository(project) ?: return

        val tagDeleteDialog = TagDeleteDialog(repository)
        if (!tagDeleteDialog.showAndGet()) {
            return
        }

        val deleteTagOptions = tagDeleteDialog.deleteTagOptions

        val flag = Messages.showOkCancelDialog(
            project,
            I18n.getContent("DeleteTagAction.confirm", deleteTagOptions.tags.size),
            I18n.getContent("DeleteTagAction.text"),
            I18n.getContent("OkText"),
            I18n.getContent("CancelText"),
            GitFlowPlusIcons.warning
        )
        if (flag != 0) {
            return
        }

        val tip = I18n.getContent("deleting.tags", deleteTagOptions.tags.size)
        object : Task.Backgroundable(project, tip, false) {

            override fun run(indicator: ProgressIndicator) {
                var i = 1
                val faction = 1.0 / deleteTagOptions.tags.size

                NotifyUtil.notifyGitCommand(project, "=====================================")
                deleteTagOptions.tags
                    .forEach {
                        gitFlowPlus.deleteTag(repository, it.tag)

                        indicator.fraction = faction * i++
                    }

                repository.update()

                project.messageBus.syncPublisher(GitRepository.GIT_REPO_CHANGE).repositoryChanged(repository)

                VirtualFileManager.getInstance().asyncRefresh(null)

                val message = I18n.getContent("deleting.tags.finished", deleteTagOptions.tags.size)
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
