package com.github.xiaolyuh.utils

import com.github.xiaolyuh.service.ConfigService
import com.github.xiaolyuh.service.GitFlowPlus
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.FileStatus
import com.intellij.openapi.vcs.changes.Change
import com.intellij.openapi.vcs.changes.ChangeListManager

object ActionUtils {

    fun shouldShow(e: AnActionEvent): Boolean {
        val project = e.project ?: return false

        val configService = ConfigService.getInstance(project)

        return configService.isInit() && configService.existsK8sOptions()
    }

    fun shouldShowSec(e: AnActionEvent): Boolean {
        val project = e.project ?: return false

        val configService = ConfigService.getInstance(project)

        return shouldShow(e) && !configService.getInitOptions().testBranchSec.isNullOrBlank()
    }

    fun shouldShowStartTestSec(event: AnActionEvent): Boolean {
        val project = event.project ?: return false

        val configService = ConfigService.getInstance(project)

        val isInit = GitBranchUtil.isGitProject(project) && configService.isInit()
        if (!isInit) {
            return false
        }

        var gitFlowPlus = GitFlowPlus.getInstance()

        val currentBranch = gitFlowPlus.getCurrentBranch(project)

        val initOptions = configService.getInitOptions()

        val featurePrefix = initOptions.featurePrefix
        val hotfixPrefix = initOptions.hotfixPrefix

        val isDevBranch = StringUtils.startsWith(currentBranch, featurePrefix)
                || StringUtils.startsWith(currentBranch, hotfixPrefix)

        return isDevBranch && !isConflicts(project) && !configService.getInitOptions().testBranchSec.isNullOrBlank()
    }

    private fun isConflicts(project: Project): Boolean {
        val changes = ChangeListManager.getInstance(project).allChanges

        if (changes.size > 1000) {
            return true
        }

        return changes.stream().anyMatch { it: Change? -> it!!.fileStatus === FileStatus.MERGED_WITH_CONFLICTS }
    }

}