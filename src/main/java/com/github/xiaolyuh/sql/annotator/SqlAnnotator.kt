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
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.elementType
import com.intellij.psi.xml.XmlText
import java.util.*

class SqlAnnotator : Annotator {
    private val errorHolderKey = Key.create<Pair<PsiErrorElement?, PsiElement?>>("gfp.annotator.errorHolder")

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        val injectionHost = InjectedLanguageManager.getInstance(element.project).getInjectionHost(element)
        if (injectionHost !is XmlText) {
            return
        }

        val pair = element.project.getUserData(errorHolderKey)
        if (pair == null) {
            val errorElement = PsiTreeUtil.findChildOfType(element.containingFile, PsiErrorElement::class.java)
            if (errorElement != null) {
                val prevValidSibling = getPrevValidSiblingOfError(errorElement)
                element.project.putUserData(errorHolderKey, Pair(errorElement, prevValidSibling))
            } else {
                element.project.putUserData(errorHolderKey, Pair(null, null))
            }
            return
        }

        if (pair.first == null || pair.second == null) {
            return
        }

        if (pair.second != element) {
            return
        }

        element.project.putUserData(errorHolderKey, null)

        analyzeSyntax(pair.first!!, pair.second!!, holder)

        return

        @Suppress("UNREACHABLE_CODE")
        analyzeSemantics(element, holder)
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

    private fun analyzeSyntax(errorElement: PsiErrorElement, prevSiblingOfError: PsiElement, holder: AnnotationHolder) {
        if (prevSiblingOfError is SqlCompoundSelectStmt && prevSiblingOfError.selectStmtList.isNotEmpty()) {
            val compoundResultColumn = prevSiblingOfError.selectStmtList[0].compoundResultColumn ?: return
            val lastChild = compoundResultColumn.lastChild
            @Suppress("DialogTitleCapitalization")
            holder.newAnnotation(HighlightSeverity.ERROR, "缺少逗号")
                .range(lastChild.textRange)
                .withFix(AddCommaQuickFix(lastChild))
                .highlightType(ProblemHighlightType.GENERIC_ERROR_OR_WARNING)
                .create()
            return
        }

        if (prevSiblingOfError.elementType == SqlTypes.COMMA && errorElement.firstChild.elementType == SqlTypes.FROM) {
            @Suppress("DialogTitleCapitalization")
            holder.newAnnotation(HighlightSeverity.ERROR, "多余的逗号")
                .range(prevSiblingOfError.textRange)
                .withFix(RemoveCommaQuickFix(prevSiblingOfError))
                .highlightType(ProblemHighlightType.GENERIC_ERROR_OR_WARNING)
                .create()
            return
        }
    }

    private fun analyzeSemantics(element: PsiElement, holder: AnnotationHolder) {
        val tableMap = getFirstConnCacheDbTables(element.project) ?: return

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

            val sqlTableName = SqlUtils.getTableNameOfAlias(aliasMap, columnTableAlias) ?: return

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
