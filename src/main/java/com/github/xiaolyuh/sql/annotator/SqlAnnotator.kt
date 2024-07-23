package com.github.xiaolyuh.sql.annotator

import com.dbn.cache.CacheDbTable
import com.github.xiaolyuh.dbn.DbnToolWindowPsiElement.Companion.getTables
import com.github.xiaolyuh.sql.psi.SqlColumnName
import com.github.xiaolyuh.sql.psi.SqlJoinClause
import com.github.xiaolyuh.sql.psi.SqlStatement
import com.github.xiaolyuh.sql.psi.SqlTableName
import com.github.xiaolyuh.utils.SqlUtils
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import java.util.*

class SqlAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        val tableMap = getTables(element.project) ?: return

        if (element is SqlTableName) {
            if (SqlUtils.isColumnTableAlias(element)) {
                annotateColumnTableAlias(element, holder)
            } else {
                annotateTableName(element, holder, tableMap)
            }
        } else if (element is SqlColumnName) {
            annotateColumnName(element, holder, tableMap)
        }
    }

    private fun annotateTableName(
        sqlTableName: SqlTableName,
        holder: AnnotationHolder,
        tableMap: Map<String, CacheDbTable>,
    ) {
        val cacheDbTable = tableMap[sqlTableName.text]
        if (cacheDbTable == null) {
            createError("无法解析表名 " + sqlTableName.text, holder, sqlTableName.textRange)
        }
    }

    private fun annotateColumnTableAlias(
        columnTableAliasName: SqlTableName,
        holder: AnnotationHolder,
    ) {
        val sqlStatement = PsiTreeUtil.getParentOfType(columnTableAliasName, SqlStatement::class.java) ?: return

        val sqlJoinClauses = PsiTreeUtil.findChildrenOfType(sqlStatement, SqlJoinClause::class.java)

        val aliasMap = SqlUtils.getAliasMap(sqlJoinClauses)
        val sqlTableAliases = aliasMap[columnTableAliasName.text]
        if (sqlTableAliases == null) {
            createError("无法解析表别名 " + columnTableAliasName.text, holder, columnTableAliasName.textRange)
        }
    }

    private fun annotateColumnName(
        sqlColumnName: SqlColumnName,
        holder: AnnotationHolder,
        tableMap: Map<String, CacheDbTable>,
    ) {
        val sqlStatement = PsiTreeUtil.getParentOfType(sqlColumnName, SqlStatement::class.java) ?: return

        val columnTableAlias = SqlUtils.getTableAliasNameOfColumn(sqlColumnName)
        if (columnTableAlias != null) {
            val sqlJoinClauses = PsiTreeUtil.findChildrenOfType(
                sqlStatement,
                SqlJoinClause::class.java
            )
            val aliasMap = SqlUtils.getAliasMap(sqlJoinClauses)

            val sqlTableName = SqlUtils.getTableNameOfAlias(aliasMap, columnTableAlias)?:return

            val cacheDbTable = tableMap[sqlTableName.text] ?: return
            val cacheDbColumn = cacheDbTable.cacheDbColumnMap[sqlColumnName.text]
            if (cacheDbColumn == null) {
                createError("无法解析列名 " + sqlColumnName.text, holder, sqlColumnName.textRange)
            }
        } else {
            val sqlTableNames = PsiTreeUtil.findChildrenOfType(
                sqlStatement,
                SqlTableName::class.java
            )

            val cacheDbTables = sqlTableNames
                .filter {
                    PsiTreeUtil.getNextSiblingOfType(it, SqlColumnName::class.java) == null
                }
                .map {
                    tableMap[it.text]
                }
                .filter { Objects.nonNull(it) }
            if (cacheDbTables.isEmpty()) {
                return
            }

            val cacheDbColumns = cacheDbTables
                .map {
                    it!!.cacheDbColumnMap[sqlColumnName.text]
                }
                .filter { Objects.nonNull(it) }

            if (cacheDbColumns.isEmpty()) {
                createError("无法解析列名 " + sqlColumnName.text, holder, sqlColumnName.textRange)
            } else if (cacheDbColumns.size > 1) {
                createError("解析到多个列名 " + sqlColumnName.text, holder, sqlColumnName.textRange)
            }
        }
    }

    private fun createError(tip: String, holder: AnnotationHolder, textRange: TextRange) {
        holder.newAnnotation(HighlightSeverity.ERROR, tip)
            .range(textRange)
            .highlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL)
            .create()
    }

}
