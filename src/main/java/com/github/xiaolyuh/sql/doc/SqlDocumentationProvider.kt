package com.github.xiaolyuh.sql.doc

import com.dbn.cache.CacheDbColumn
import com.dbn.cache.CacheDbTable
import com.github.xiaolyuh.dbn.DbnToolWindowPsiElement
import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.lang.documentation.DocumentationMarkup.*
import com.intellij.psi.PsiElement
import org.jetbrains.annotations.Nls
import java.util.*
import kotlin.streams.toList

class SqlDocumentationProvider : AbstractDocumentationProvider() {

    override fun generateDoc(element: PsiElement, originalElement: PsiElement?): @Nls String? {
        if (element !is DbnToolWindowPsiElement) {
            return null
        }

        val cacheDbTableMap = DbnToolWindowPsiElement.getTables(element.project) ?: return null

        if (element.columnName == null) {
            val tableNames = element.tableNames
            val tableName = tableNames.iterator().next()
            val cacheDbTable = cacheDbTableMap[tableName] ?: return null
            return generateTableDoc(cacheDbTable)
        } else {
            return element.tableNames.stream()
                .map {
                    val cacheDbTable = cacheDbTableMap[it] ?: return@map null

                    val cacheDbColumn = cacheDbTable.cacheDbColumnMap[element.columnName] ?: return@map null

                    generateColumnDoc(cacheDbTable, cacheDbColumn)
                }
                .filter(Objects::nonNull)
                .toList()
                .firstOrNull()
        }
    }

    private fun generateColumnDoc(table: CacheDbTable, column: CacheDbColumn): String {
        return buildString {
            append("<span>")
            append(column.name)
            append(" ")
            append(column.cacheDbDataType.qualifiedName)
            append(" ")
            append(column.columnDefault)
            append(" ")
            append(column.columnComment)
            append("</span>")

            append("<span style='color:gray'>")
            append(table.name)
            append("(")
            append(table.comment)
            append(")")
            append("</span>")
        }
    }

    private fun generateTableDoc(table: CacheDbTable): String {
        return buildString {
            append(DEFINITION_START)
            append(table.name)
            append("(")
            append(table.comment)
            append(")")
            append(DEFINITION_END)

            append(SECTIONS_START)

            for (column in table.cacheDbColumnMap.values) {
                append("<tr><td valign='top' class='section' style='color:black'><p>")
                append(column.name)
                append("</p>")
                append("</td><td valign='top' style='color:gray'>")
                append(column.cacheDbDataType.qualifiedName)
                append(" ")
                append(column.columnDefault)
                append(" ")
                append(column.columnComment)
                append("</td>")
                append("</tr>\r\n")
            }

            append(SECTIONS_END)
        }
    }
}
