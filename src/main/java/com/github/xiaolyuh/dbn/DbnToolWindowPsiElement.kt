package com.github.xiaolyuh.dbn

import com.dbn.browser.DatabaseBrowserManager
import com.dbn.connection.ConnectionHandler
import com.dbn.editor.data.options.DataEditorSettings
import com.dbn.`object`.DBSchema
import com.dbn.`object`.DBTable
import com.dbn.`object`.common.DBObjectBundle
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.lang.ASTNode
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.project.Project
import java.net.URI


class DbnToolWindowPsiElement(val tableNames: Set<String>, val columnName: String?, node: ASTNode) :
    ASTWrapperPsiElement(node) {

    override fun navigate(requestFocus: Boolean) {
        val dbSchema = getDefaultDbScheme(project) ?: return

        if (tables == null) {
            tables = dbSchema.tables
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
            return
        }

        browserManager.navigateToElement(dbColumns[0], requestFocus, true)
    }

    companion object {
        private var tables: List<DBTable>? = null
        fun getTables(): List<DBTable>? {
            return tables
        }

        fun getDefaultDbScheme(project: Project): DBSchema? {
            if (!dbnAvailable()) {
                return null
            }

            val browserManager = DatabaseBrowserManager.getInstance(project)
            val connection = browserManager.selectedConnection
            if (!ConnectionHandler.isLiveConnection(connection)) {
                return null
            }

            val dbName = getDbName(project) ?: return null
            val objectBundle: DBObjectBundle = connection?.objectBundle ?: return null

            return objectBundle.schemas.firstOrNull { it.name == dbName }
        }

        private fun getDbName(project: Project): String? {
            val browserManager = DatabaseBrowserManager.getInstance(project)
            val connection = browserManager.selectedConnection
            if (!ConnectionHandler.isLiveConnection(connection)) {
                return null
            }

            val connectionSettingsList = DataEditorSettings.getInstance(project).parent!!.connectionSettings
            val connectionSettings = connectionSettingsList.connections[0]

            return resolveUrlDbName(connectionSettings.databaseSettings.connectionUrl)
        }

        private fun resolveUrlDbName(url: String): String {
            val uri = URI.create(url.substring(5))
            return uri.path.substring(1)
        }

        private fun dbnAvailable(): Boolean {
            val pluginId = PluginId.getId("DBN")
            val pluginDescriptor = PluginManagerCore.getPlugin(pluginId) ?: return false
            return pluginDescriptor.isEnabled
        }
    }
}