package com.github.xiaolyuh.listener

import com.github.xiaolyuh.consts.Constants
import com.github.xiaolyuh.logger.GitFlowPlusLogger.logInfo
import com.github.xiaolyuh.service.ConfigService.Companion.getInstance
import com.intellij.ide.impl.ProjectUtil
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent

class ConfigBulkFileListener() : BulkFileListener {

    override fun after(events: MutableList<out VFileEvent>) {
        for (event in events) {
            val file = event.file
            if (file == null) {
                continue
            }

            if (!Constants.INTEREST_FILES.contains(file.name)) {
                continue
            }

            val project = ProjectUtil.getActiveProject() ?: return

            val basePath = project.basePath ?: return

            if (!file.path.startsWith(basePath)) {
                continue
            }

            logInfo("${project.name}项目配置文件发生变化:$file")

            val configService = getInstance(project)

            configService.reloadConfigIfNeeded()
        }
    }

}
