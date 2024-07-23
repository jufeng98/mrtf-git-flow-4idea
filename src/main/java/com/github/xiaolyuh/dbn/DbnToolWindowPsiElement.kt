package com.github.xiaolyuh.dbn

import com.dbn.browser.DatabaseBrowserManager
import com.dbn.cache.CacheDbTable
import com.dbn.cache.MetadataCacheService
import com.dbn.connection.ConnectionHandler
import com.dbn.connection.config.ConnectionDatabaseSettings
import com.dbn.editor.data.options.DataEditorSettings
import com.dbn.`object`.DBSchema
import com.dbn.`object`.common.DBObjectBundle
import com.github.xiaolyuh.utils.TooltipUtils
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import org.apache.commons.collections4.CollectionUtils
import java.net.URI


class DbnToolWindowPsiElement(val tableNames: Set<String>, val columnName: String?, node: ASTNode) :
    ASTWrapperPsiElement(node) {

    override fun navigate(requestFocus: Boolean) {
        val dbSchema = getDbSchemeOfFirstConnection(project)
        if (dbSchema == null) {
            TooltipUtils.showTooltip(
                "无法跳转DB Browser工具窗口,请先连接数据库,或者打开 Connect Automatically 选项!",
                project
            )
            return
        }

        val tables = dbSchema.tables
        if (tables.isNullOrEmpty()) {
            TooltipUtils.showTooltip("尝试初始化tables控件,请稍后再试...", project)
            return
        }

        val dbTables = dbSchema.tables.filter { tableNames.contains(it.name) }
        if (dbTables.isEmpty()) {
            TooltipUtils.showTooltip("${dbSchema.name}数据库中未找到表$tableNames", project)
            return
        }

        val browserManager = DatabaseBrowserManager.getInstance(getProject())
        if (columnName == null) {
            browserManager.navigateToElement(dbTables[0], requestFocus, true)
            return
        }

        var dbColumns = dbTables.map { it.columns }.flatten()
        if (dbColumns.isEmpty()) {
            TooltipUtils.showTooltip("尝试初始化columns组件,请稍后再试...", project)
            return
        }

        dbColumns = dbColumns.filter { it.name == columnName }
        if (dbColumns.isEmpty()) {
            TooltipUtils.showTooltip("在${tableNames}中未能解析列$columnName,无法跳转!", project)
        } else if (dbColumns.size == 1) {
            browserManager.navigateToElement(dbColumns[0], requestFocus, true)
        } else {
            TooltipUtils.showTooltip("在${tableNames}中解析到多个列$columnName,无法跳转!", project)
        }
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

        private val dbNameKey = Key.create<String>("gfp.connection.url.dbName")

        fun getFirstConnectionConfigDbName(project: Project): String? {
            val config = getFirstConnectionConfig(project) ?: return null
            val url = config.connectionUrl ?: return null
            var dbName = project.getUserData(dbNameKey)
            if (dbName != null) {
                return dbName
            }

            dbName = resolveUrlDbName(url)
            project.putUserData(dbNameKey, dbName)

            return dbName
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