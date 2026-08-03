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

    fun shouldShowStaging(e: AnActionEvent): Boolean {
        val project = e.project ?: return false

        val configService = ConfigService.getInstance(project)

        return configService.isInit() && !ConfigService.getInstance(project)
            .getInitOptions().stagingBranch.isNullOrBlank()
    }

    fun shouldShowSec(e: AnActionEvent): Boolean {
        val project = e.project ?: return false

        return shouldShow(e) && !ConfigService.getInstance(project).getInitOptions().testBranchSec.isNullOrBlank()
    }

    fun isDevBranch(event: AnActionEvent): Boolean {
        val project = event.project ?: return false

        val currentBranch = GitFlowPlus.getInstance().getCurrentBranch(project) ?: return false

        val initOptions = ConfigService.getInstance(project).getInitOptions()

        return StringUtils.startsWith(currentBranch, initOptions.featurePrefix)
                || StringUtils.startsWith(currentBranch, initOptions.hotfixPrefix)
    }

}