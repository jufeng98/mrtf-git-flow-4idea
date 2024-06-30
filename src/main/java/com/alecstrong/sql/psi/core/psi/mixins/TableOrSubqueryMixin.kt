package com.alecstrong.sql.psi.core.psi.mixins

import com.alecstrong.sql.psi.core.psi.QueryElement.QueryResult
import com.alecstrong.sql.psi.core.psi.SqlCompositeElementImpl
import com.github.xiaolyuh.sql.psi.SqlTableOrSubquery
import com.intellij.lang.ASTNode

internal abstract class TableOrSubqueryMixin(
    node: ASTNode
) : SqlCompositeElementImpl(node),
    SqlTableOrSubquery {
    private val queryExposed: Collection<QueryResult> by com.alecstrong.sql.psi.core.ModifiableFileLazy(containingFile) lazy@{
        tableName?.let { tableNameElement ->
            val result = tableAvailable(tableNameElement, tableNameElement.name)
            if (result.isEmpty()) {
                return@lazy emptyList<QueryResult>()
            }
            tableAlias?.let { alias ->
                return@lazy listOf(QueryResult(alias, result.flatMap { it.columns }))
            }
            return@lazy result
        }
        compoundSelectStmt?.let {
            val result = it.queryExposed()
            tableAlias?.let { alias ->
                return@lazy result.map { it.copy(table = alias) }
            }
            return@lazy result
        }
        joinClause?.let { return@lazy it.queryExposed() }
        return@lazy tableOrSubqueryList.flatMap { it.queryExposed() }
    }

    override fun queryExposed() = queryExposed
}