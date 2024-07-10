package com.github.xiaolyuh.sql.doc

import com.dbn.`object`.DBColumn
import com.dbn.`object`.DBTable
import com.github.xiaolyuh.dbn.DbnToolWindowPsiElement
import com.github.xiaolyuh.dbn.DbnToolWindowPsiElement.Companion.getTables
import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.lang.documentation.DocumentationMarkup.*
import com.intellij.psi.PsiElement
import org.jetbrains.annotations.Nls

class SqlDocumentationProvider : AbstractDocumentationProvider() {
    override fun generateDoc(element: PsiElement, originalElement: PsiElement?): @Nls String? {
        if (element !is DbnToolWindowPsiElement) {
            return null
        }

        val tables: List<DBTable>? = getTables(element.project)
        tables ?: return null

        val columnName = element.columnName
        if (columnName == null) {
            val tableNames = element.tableNames
            val tableName = tableNames.iterator().next()
            val table = tables.firstOrNull { it.name == tableName } ?: return null
            return generateTableDoc(table)
        } else {
            tables.forEach { table ->
                table.columns.forEach {
                    if (it.name == columnName) {
                        return generateColumnDoc(table, it)
                    }
                }
            }

            return null
        }
    }

    private fun generateColumnDoc(table: DBTable, column: DBColumn): String {
        return buildString {
            append("<span>")
            append(column.name + " " + column.dataType.qualifiedName + " " + column.columnComment)
            append("</span>")

            append("<span style='color:gray'>")
            append(table.name + "(" + table.comment + ")")
            append("</span>")
        }
    }

    private fun generateTableDoc(table: DBTable): String {
        return buildString {
            append(DEFINITION_START)
            append(table.name + "(" + table.comment + ")")
            append(DEFINITION_END)

            append(SECTIONS_START)

            for (column in table.columns) {
                append("<tr><td valign='top' class='section' style='color:black'><p>")
                append(column.name)
                append("</p>")
                append("</td><td valign='top' style='color:gray'>")
                append(column.dataType.qualifiedName + " " + column.columnComment)
                append("</td>")
                append("</tr>\r\n")
            }

            append(SECTIONS_END)
        }
    }
}
