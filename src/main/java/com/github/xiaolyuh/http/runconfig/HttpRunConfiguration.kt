package com.github.xiaolyuh.http.runconfig

import com.intellij.execution.Executor
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.RunConfigurationBase
import com.intellij.execution.configurations.RunConfigurationOptions
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import org.jdom.Element

class HttpRunConfiguration(
    project: Project,
    httpConfigurationFactory: HttpConfigurationFactory,
    name: String?,
) :
    RunConfigurationBase<HttpRunConfiguration>(
        project,
        httpConfigurationFactory,
        name
    ) {
    lateinit var httpFilePath: String
    lateinit var env: String

    override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState {
        return HttpRunProfileState(project, environment)
    }

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> {
        return HttpSettingsEditor()
    }

    override fun writeExternal(element: Element) {
        super.writeExternal(element)
    }

    override fun readExternal(element: Element) {
        super.readExternal(element)
    }
}
