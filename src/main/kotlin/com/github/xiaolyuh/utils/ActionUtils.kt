package com.github.xiaolyuh.utils

import com.github.xiaolyuh.service.ConfigService
import com.github.xiaolyuh.service.GitFlowPlus
import com.intellij.openapi.actionSystem.AnActionEvent

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

        if (configService.getInitOptions().testBranchSec.isNullOrBlank()) {
            return false
        }

        var gitFlowPlus = GitFlowPlus.getInstance()

        val currentBranch = gitFlowPlus.getCurrentBranch(project)

        val initOptions = configService.getInitOptions()

        val featurePrefix = initOptions.featurePrefix
        val hotfixPrefix = initOptions.hotfixPrefix

        val isDevBranch = StringUtils.startsWith(currentBranch, featurePrefix)
                || StringUtils.startsWith(currentBranch, hotfixPrefix)

        return isDevBranch
    }

}