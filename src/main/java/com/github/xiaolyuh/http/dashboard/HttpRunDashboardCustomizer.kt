package com.github.xiaolyuh.http.dashboard

import com.github.xiaolyuh.http.runconfig.HttpRunConfiguration
import com.github.xiaolyuh.http.runconfig.HttpRunProfileState.Companion.getTargetHttpMethod
import com.intellij.execution.RunnerAndConfigurationSettings
import com.intellij.execution.dashboard.RunDashboardCustomizer
import com.intellij.execution.dashboard.RunDashboardRunConfigurationNode
import com.intellij.execution.ui.RunContentDescriptor
import com.intellij.psi.PsiElement

/**
 * yudong
 */
class HttpRunDashboardCustomizer : RunDashboardCustomizer() {

    override fun isApplicable(settings: RunnerAndConfigurationSettings, descriptor: RunContentDescriptor?): Boolean {
        return settings.configuration is HttpRunConfiguration
    }

    override fun getPsiElement(node: RunDashboardRunConfigurationNode): PsiElement? {
        val configuration = node.configurationSettings.configuration as HttpRunConfiguration
        return getTargetHttpMethod(configuration.httpFilePath, configuration.name, configuration.project)
    }

}
