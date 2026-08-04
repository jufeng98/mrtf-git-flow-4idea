package com.github.xiaolyuh.utils

import com.github.xiaolyuh.service.ConfigService
import com.github.xiaolyuh.service.GitBranchService
import com.github.xiaolyuh.service.GitFlowPlus
import com.intellij.openapi.actionSystem.AnActionEvent
import java.util.Objects

object ActionUtils {

    fun shouldShow(e: AnActionEvent): Boolean {
        val project = e.project ?: return false

        val configService = ConfigService.getInstance(project)

        return configService.isInit() && configService.existsK8sOptions()
    }

    fun shouldShowStaging(event: AnActionEvent): Boolean {
        val project = event.project ?: return false

        val configService = ConfigService.getInstance(project)

        val isInit = GitBranchService.isGitProject(project) && configService.isInit()
        if (!isInit) {
            return false
        }

        val noStaging = configService.getInitOptions().stagingBranch.isNullOrBlank()
        if (noStaging) {
            return false
        }

        // 已经初始化并且前缀是开发分支才可用
        return isDevBranch(event)
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

    fun isStagingBranch(event: AnActionEvent): Boolean {
        val project = event.project ?: return false

        val currentBranch = GitFlowPlus.getInstance().getCurrentBranch(project) ?: return false

        val initOptions = ConfigService.getInstance(project).getInitOptions()

        return Objects.equals(currentBranch, initOptions.stagingBranch)
    }

}