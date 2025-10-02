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

        return shouldShow(e) && !ConfigService.getInstance(project).getInitOptions().testBranchSec.isNullOrBlank()
    }

    fun isDevBranch(event: AnActionEvent): Boolean {
        val project = event.project ?: return false

        val currentBranch = GitFlowPlus.getInstance().getCurrentBranch(project)

        val initOptions = ConfigService.getInstance(project).getInitOptions()

        val featurePrefix = initOptions.featurePrefix
        val hotfixPrefix = initOptions.hotfixPrefix

        return StringUtils.startsWith(currentBranch, featurePrefix)
                || StringUtils.startsWith(currentBranch, hotfixPrefix)
    }

}