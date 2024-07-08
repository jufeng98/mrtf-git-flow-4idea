package com.github.xiaolyuh.dbn

import com.dbn.browser.DatabaseBrowserManager
import com.dbn.connection.ConnectionHandler
import com.dbn.editor.data.options.DataEditorSettings
import com.dbn.`object`.common.DBObjectBundle
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.lang.ASTNode
import com.intellij.openapi.extensions.PluginId
import java.net.URI


class DbnToolWindowPsiElement(private val tableName: String, private val columnName: String?, node: ASTNode) :
    ASTWrapperPsiElement(node) {

    override fun navigate(requestFocus: Boolean) {
        val pluginId = PluginId.getId("DBN")
        val pluginDescriptor = PluginManagerCore.getPlugin(pluginId) ?: return
        if (!pluginDescriptor.isEnabled) {
            return
        }

        val browserManager = DatabaseBrowserManager.getInstance(getProject())
        val connection = browserManager.selectedConnection
        if (!ConnectionHandler.isLiveConnection(connection)) {
            return
        }

        val objectBundle: DBObjectBundle = connection?.objectBundle ?: return

        val connectionSettingsList = DataEditorSettings.getInstance(project).parent!!.connectionSettings
        val connectionSettings = connectionSettingsList.connections[0]

        val dbName = resolveUrlDbName(connectionSettings.databaseSettings.connectionUrl)

        val schemas = objectBundle.schemas.filter { it.name == dbName }.toList()
        if (schemas.isEmpty()) {
            return
        }

        val dbSchema = schemas[0]
        val dbTables = dbSchema.tables.filter { it.name == tableName }.toList()
        if (dbTables.isEmpty()) {
            return
        }

        val tableTreeNode = dbTables[0]
        if (columnName == null) {
            browserManager.navigateToElement(tableTreeNode, requestFocus, true)
            return
        }
    }

    private fun resolveUrlDbName(url: String): String {
        val uri = URI.create(url.substring(5))
        return uri.path.substring(1)
    }
}