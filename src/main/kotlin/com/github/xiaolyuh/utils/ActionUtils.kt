package com.github.xiaolyuh.utils

import com.github.xiaolyuh.service.ConfigService
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

}