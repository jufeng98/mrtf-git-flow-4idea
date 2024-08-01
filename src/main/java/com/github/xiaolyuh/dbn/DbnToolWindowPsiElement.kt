package com.github.xiaolyuh.dbn

import com.dbn.browser.DatabaseBrowserManager
import com.dbn.cache.CacheDbTable
import com.dbn.cache.MetadataCacheService
import com.github.xiaolyuh.utils.TooltipUtils
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindowManager


class DbnToolWindowPsiElement(val tableNames: Set<String>, val columnName: String?, node: ASTNode) :
    ASTWrapperPsiElement(node) {

    override fun navigate(requestFocus: Boolean) {
        val browserManager = DatabaseBrowserManager.getInstance(project)
        val dbSchema = browserManager.switchToFirstConnectionAndGetDbScheme(project)
        if (dbSchema == null) {
            TooltipUtils.showTooltip("无法跳转DB Browser工具窗口,请先连接数据库!", project)

            val toolWindowManager = ToolWindowManager.getInstance(project)
            val toolWindow = toolWindowManager.getToolWindow("DB Browser")
            toolWindow?.show()
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
        fun getFirstConnCacheDbTables(project: Project): Map<String, CacheDbTable>? {
            val cacheService = MetadataCacheService.getService(project)
            return cacheService.getFirstConnectionDBCacheTables(project)
        }
    }
}