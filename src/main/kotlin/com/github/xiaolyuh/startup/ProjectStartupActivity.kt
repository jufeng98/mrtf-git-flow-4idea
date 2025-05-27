package com.github.xiaolyuh.startup

import com.github.xiaolyuh.service.ConfigService.Companion.getInstance
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

/**
 * @author yudong
 */
class ProjectStartupActivity : ProjectActivity {

    override suspend fun execute(project: Project) {
        val configService = getInstance(project)

        configService.tryInitConfig()
    }

}
