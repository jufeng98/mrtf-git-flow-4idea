package com.github.xiaolyuh.dbn

import com.dbn.browser.DatabaseBrowserManager
import com.dbn.cache.CacheDbTable
import com.dbn.cache.MetadataCacheService
import com.dbn.connection.ConnectionHandler
import com.dbn.connection.config.ConnectionDatabaseSettings
import com.dbn.editor.data.options.DataEditorSettings
import com.dbn.`object`.DBSchema
import com.dbn.`object`.common.DBObjectBundle
import com.intellij.codeInsight.hint.HintManager
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import org.apache.commons.collections4.CollectionUtils
import java.net.URI


class DbnToolWindowPsiElement(val tableNames: Set<String>, val columnName: String?, node: ASTNode) :
    ASTWrapperPsiElement(node) {

    override fun navigate(requestFocus: Boolean) {
        val dbSchema = getDbSchemeOfFirstConnection(project)
        if (dbSchema == null) {
            showTooltip("无法跳转DB Browser工具窗口,请先连接数据库,或者打开 Connect Automatically 选项!", project)
            return
        }

        val tables = dbSchema.tables
        if (tables.isNullOrEmpty()) {
            showTooltip("尝试初始化tables控件,请稍后再试...", project)
            return
        }

        val dbTables = dbSchema.tables.filter { tableNames.contains(it.name) }
        if (dbTables.isEmpty()) {
            return
        }

        val browserManager = DatabaseBrowserManager.getInstance(getProject())
        if (columnName == null) {
            browserManager.navigateToElement(dbTables[0], requestFocus, true)
            return
        }

        val dbColumns = dbTables.map { it.columns }.flatten().filter { it.name == columnName }
        if (dbColumns.isEmpty()) {
            showTooltip("尝试初始化columns组件,请稍后再试...", project)
            return
        }

        browserManager.navigateToElement(dbColumns[0], requestFocus, true)
    }

    private fun showTooltip(msg: String, project: Project) {
        val editor = FileEditorManager.getInstance(project).selectedTextEditor ?: return
        HintManager.getInstance().showInformationHint(editor, msg)
    }

    companion object {
        fun getTables(project: Project): Map<String, CacheDbTable>? {
            val dbName = getFirstConnectionConfigDbName(project) ?: return null

            val cacheService = MetadataCacheService.getService(project)
            return cacheService.schemaMap[dbName]
        }

        fun getDbSchemeOfFirstConnection(project: Project): DBSchema? {
            val browserManager = DatabaseBrowserManager.getInstance(project)

            val config = getFirstConnectionConfig(project) ?: return null
            browserManager.selectConnection(config.connectionId)

            val connection = browserManager.selectedConnection
            if (!ConnectionHandler.isLiveConnection(connection)) {
                return null
            }

            val dbName = getFirstConnectionConfigDbName(project) ?: return null
            val objectBundle: DBObjectBundle = connection?.objectBundle ?: return null

            return objectBundle.schemas.firstOrNull { it.name == dbName }
        }

        private fun getFirstConnectionConfig(project: Project): ConnectionDatabaseSettings? {
            val projectSettings = DataEditorSettings.getInstance(project).parent ?: return null
            val connectionSettingsList = projectSettings.connectionSettings ?: return null
            val connections = connectionSettingsList.connections
            if (CollectionUtils.isEmpty(connections)) {
                return null
            }

            val connectionSettings = connections[0]
            return connectionSettings?.databaseSettings ?: return null
        }

        fun getFirstConnectionConfigDbName(project: Project): String? {
            val config = getFirstConnectionConfig(project) ?: return null
            val url = config.connectionUrl ?: return null
            return resolveUrlDbName(url)
        }

        private fun resolveUrlDbName(url: String): String? {
            try {
                val uri = URI.create(url.substring(5))
                return uri.path.substring(1)
            } catch (e: Exception) {
                return null
            }
        }

    }
}