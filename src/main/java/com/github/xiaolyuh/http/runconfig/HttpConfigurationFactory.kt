package com.github.xiaolyuh.http.runconfig

import com.github.xiaolyuh.http.HttpIcons
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.SimpleConfigurationType
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.NotNullLazyValue

class HttpConfigurationFactory : SimpleConfigurationType(
    "gitFlowPlusHttpClient",
    "HttpClient",
    "Use to send request",
    NotNullLazyValue.createConstantValue(HttpIcons.FILE)
) {
    override fun createTemplateConfiguration(project: Project): RunConfiguration {
        return HttpRunConfiguration(project, this, "")
    }

}
