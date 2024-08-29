package com.github.xiaolyuh.http.runconfig

import com.github.xiaolyuh.http.HttpIcons
import com.intellij.execution.configurations.ConfigurationTypeBase
import com.intellij.openapi.util.NotNullLazyValue

class HttpConfigurationType : ConfigurationTypeBase(
    "gitFlowPlusHttpClient",
    "HttpClient",
    "Use to send request",
    NotNullLazyValue.createConstantValue(HttpIcons.FILE)
) {
    init {
        addFactory(HttpConfigurationFactory())
    }
}
