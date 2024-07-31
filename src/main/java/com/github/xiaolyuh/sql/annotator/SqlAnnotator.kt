package com.github.xiaolyuh.sql.annotator

import com.dbn.cache.CacheDbTable
import com.github.xiaolyuh.dbn.DbnToolWindowPsiElement.Companion.getFirstConnCacheDbTables
import com.github.xiaolyuh.sql.psi.*
import com.github.xiaolyuh.utils.SqlUtils
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.elementType
import com.intellij.psi.xml.XmlText
import java.util.*

class SqlAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element !is SqlRoot) {
            return
        }

        val injectionHost = InjectedLanguageManager.getInstance(element.project).getInjectionHost(element)
        if (injectionHost !is XmlText) {
            analyzeSemantics(element, holder)
            return
        }

        val errorElement = PsiTreeUtil.findChildOfType(element.containingFile, PsiErrorElement::class.java)
        if (errorElement == null) {
            analyzeSemantics(element, holder)
            return
        }

        val prevValidSibling = getPrevValidSiblingOfError(errorElement) ?: return

        analyzeSyntax(errorElement, prevValidSibling, holder, element)
    }

    private fun getPrevValidSiblingOfError(element: PsiErrorElement): PsiElement? {
        var prevSibling: PsiElement? = element.prevSibling
        while (prevSibling != null) {
            if (prevSibling !is PsiWhiteSpace) {
                return prevSibling
            }
            prevSibling = prevSibling.prevSibling
        }
        return null
    }

    private fun analyzeSyntax(
        errorElement: PsiErrorElement,
        prevSiblingOfError: PsiElement,
        holder: AnnotationHolder,
        sqlRoot: SqlRoot,
    ) {
        if (prevSiblingOfError is SqlCompoundSelectStmt
            && prevSiblingOfError.selectStmtList.isNotEmpty()
            && errorElement.firstChild.elementType == SqlTypes.AS
            && sqlRoot.textRange.contains(prevSiblingOfError.textRange)
        ) {
            val lastElement = SqlUtils.getLastChildElement(prevSiblingOfError)
            @Suppress("DialogTitleCapitalization")
            holder.newAnnotation(HighlightSeverity.ERROR, "有语法错误!")
                .range(lastElement.textRange)
                .highlightType(ProblemHighlightType.GENERIC_ERROR_OR_WARNING)
                .create()
            return
        }

        if (prevSiblingOfError.elementType == SqlTypes.COMMA
            && errorElement.firstChild.elementType == SqlTypes.FROM
            && sqlRoot.textRange.contains(prevSiblingOfError.textRange)
        ) {
            @Suppress("DialogTitleCapitalization")
            holder.newAnnotation(HighlightSeverity.ERROR, "多余的逗号")
                .range(prevSiblingOfError.textRange)
                .withFix(RemoveCommaQuickFix(prevSiblingOfError))
                .highlightType(ProblemHighlightType.GENERIC_ERROR_OR_WARNING)
                .create()
            return
        }

        if (prevSiblingOfError.elementType == SqlTypes.DOT
            && prevSiblingOfError.prevSibling is SqlCompoundSelectStmt
            && sqlRoot.textRange.contains(prevSiblingOfError.textRange)
        ) {
            @Suppress("DialogTitleCapitalization")
            holder.newAnnotation(HighlightSeverity.ERROR, "多余的句号")
                .range(prevSiblingOfError.textRange)
                .highlightType(ProblemHighlightType.GENERIC_ERROR_OR_WARNING)
                .create()
            return
        }
    }

    private fun analyzeSemantics(element: SqlRoot, holder: AnnotationHolder) {
        val tableMap = getFirstConnCacheDbTables(element.project) ?: return
        val statement = element.statement

        val sqlCompoundSelectStmts = PsiTreeUtil.findChildrenOfType(statement, SqlCompoundSelectStmt::class.java)

        val aliasMap: MutableMap<String, List<SqlTableAlias>> = mutableMapOf()
        sqlCompoundSelectStmts.forEach { compoundSelectStmt ->
            compoundSelectStmt?.selectStmtList?.forEach {
                val sqlJoinClauses = PsiTreeUtil.findChildrenOfType(it, SqlJoinClause::class.java)
                val map = SqlUtils.getAliasMap(sqlJoinClauses)
                aliasMap.putAll(map)
            }
        }

        sqlCompoundSelectStmts.forEach { compoundSelectStmt ->
            compoundSelectStmt?.selectStmtList?.forEach {
                annotatorSelect(it, holder, tableMap, aliasMap)
            }
        }

        val updateStmt = statement.updateStmtLimited
        if (updateStmt != null) {
            annotatorUpdate(updateStmt, holder, tableMap)
        }

        val sqlInsertStmt = statement.insertStmt
        if (sqlInsertStmt != null) {
            annotatorInsert(sqlInsertStmt, holder, tableMap)
        }

        val deleteStmt = statement.deleteStmtLimited
        if (deleteStmt != null) {
            annotatorDelete(deleteStmt, holder, tableMap)
        }

    }

    private fun annotatorDelete(
        deleteStmt: SqlDeleteStmtLimited,
        holder: AnnotationHolder,
        tableMap: Map<String, CacheDbTable>,
    ) {
        val sqlTableNames = PsiTreeUtil.findChildrenOfType(deleteStmt, SqlTableName::class.java)
        sqlTableNames.forEach {
            annotateTableName(it, holder, tableMap)
        }

        val sqlColumnNames = PsiTreeUtil.findChildrenOfType(deleteStmt, SqlColumnName::class.java)
        sqlColumnNames.forEach {
            val tableName = deleteStmt.qualifiedTableName.tableName
            annotateColumnName(it, holder, tableMap, tableName)
        }
    }

    private fun annotatorInsert(
        sqlInsertStmt: SqlInsertStmt,
        holder: AnnotationHolder,
        tableMap: Map<String, CacheDbTable>,
    ) {
        val sqlTableNames = PsiTreeUtil.findChildrenOfType(sqlInsertStmt, SqlTableName::class.java)
        sqlTableNames.forEach {
            annotateTableName(it, holder, tableMap)
        }

        val sqlColumnNames = PsiTreeUtil.findChildrenOfType(sqlInsertStmt, SqlColumnName::class.java)
        sqlColumnNames.forEach {
            val tableName = sqlInsertStmt.tableName
            annotateColumnName(it, holder, tableMap, tableName)
        }
    }

    private fun annotatorUpdate(
        sqlUpdateStmtLimited: SqlUpdateStmtLimited,
        holder: AnnotationHolder,
        tableMap: Map<String, CacheDbTable>,
    ) {
        val sqlTableNames = PsiTreeUtil.findChildrenOfType(sqlUpdateStmtLimited, SqlTableName::class.java)
        sqlTableNames.forEach {
            annotateTableName(it, holder, tableMap)
        }

        val sqlColumnNames = PsiTreeUtil.findChildrenOfType(sqlUpdateStmtLimited, SqlColumnName::class.java)
        sqlColumnNames.forEach {
            val tableName = sqlUpdateStmtLimited.qualifiedTableName.tableName
            annotateColumnName(it, holder, tableMap, tableName)
        }
    }

    private fun annotatorSelect(
        sqlSelectStmt: SqlSelectStmt,
        holder: AnnotationHolder,
        tableMap: Map<String, CacheDbTable>,
        aliasMap: MutableMap<String, List<SqlTableAlias>>,
    ) {
        val sqlTableNames = PsiTreeUtil.findChildrenOfType(sqlSelectStmt, SqlTableName::class.java)
        sqlTableNames.forEach {
            if (SqlUtils.isColumnTableAlias(it)) {
                annotateColumnTableAlias(it, holder, aliasMap)
            } else {
                annotateTableName(it, holder, tableMap)
            }
        }

        val sqlColumnNames = PsiTreeUtil.findChildrenOfType(sqlSelectStmt, SqlColumnName::class.java)
        sqlColumnNames.forEach {
            annotateColumnName(it, holder, tableMap, sqlSelectStmt)
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
        aliasMap: MutableMap<String, List<SqlTableAlias>>,
    ) {

        val aliasName = columnTableAliasName.text
        val sqlTableAliases = aliasMap[aliasName]
        if (sqlTableAliases == null) {
            createError("无法解析表别名 $aliasName", holder, columnTableAliasName.textRange)
        }
    }

    private fun annotateColumnName(
        sqlColumnName: SqlColumnName,
        holder: AnnotationHolder,
        tableMap: Map<String, CacheDbTable>,
        sqlTableName: SqlTableName,
    ) {
        val cacheDbTable = tableMap[sqlTableName.text] ?: return
        val cacheDbColumn = cacheDbTable.cacheDbColumnMap[sqlColumnName.text]
        if (cacheDbColumn == null) {
            createError("无法解析列名 " + sqlColumnName.text, holder, sqlColumnName.textRange)
        }
    }

    private fun annotateColumnName(
        sqlColumnName: SqlColumnName,
        holder: AnnotationHolder,
        tableMap: Map<String, CacheDbTable>,
        sqlSelectStmt: SqlSelectStmt,
    ) {
        val columnAlias = SqlUtils.getColumnAliasIfInOrderBy(sqlColumnName)
        if (columnAlias != null) {
            return
        }

        val columnTableAlias = SqlUtils.getTableAliasNameOfColumn(sqlColumnName)
        if (columnTableAlias != null) {
            val sqlJoinClauses = PsiTreeUtil.findChildrenOfType(
                sqlSelectStmt,
                SqlJoinClause::class.java
            )
            val aliasMap = SqlUtils.getAliasMap(sqlJoinClauses)

            val sqlTableName = SqlUtils.getTableNameOfAlias(aliasMap, columnTableAlias) ?: return

            val cacheDbTable = tableMap[sqlTableName.text] ?: return
            val cacheDbColumn = cacheDbTable.cacheDbColumnMap[sqlColumnName.text]
            if (cacheDbColumn == null) {
                createError("无法解析列名 " + sqlColumnName.text, holder, sqlColumnName.textRange)
            }
        } else {
            val sqlTableNames = PsiTreeUtil.findChildrenOfType(
                sqlSelectStmt,
                SqlTableName::class.java
            )

            val cacheDbTables = sqlTableNames
                .filter {
                    !SqlUtils.isColumnTableAlias(it)
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
