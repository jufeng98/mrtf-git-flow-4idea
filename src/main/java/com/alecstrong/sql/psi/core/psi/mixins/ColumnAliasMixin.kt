package com.alecstrong.sql.psi.core.psi.mixins

import com.alecstrong.sql.psi.core.psi.SqlNamedElementImpl
import com.github.xiaolyuh.sql.parser.SqlParser
import com.github.xiaolyuh.sql.psi.SqlColumnAlias
import com.github.xiaolyuh.sql.psi.SqlCompoundSelectStmt
import com.github.xiaolyuh.sql.psi.SqlCteTableName
import com.github.xiaolyuh.sql.psi.SqlWithClause
import com.intellij.lang.ASTNode
import com.intellij.lang.PsiBuilder
import com.intellij.psi.PsiElement

internal abstract class ColumnAliasMixin(
    node: ASTNode
) : SqlNamedElementImpl(node),
    SqlColumnAlias {
    override val parseRule: (PsiBuilder, Int) -> Boolean = SqlParser::column_alias

    override fun source(): PsiElement {
        parent.let {
            return when (it) {
                is ResultColumnMixin -> it.expr!!

                is SqlCteTableName -> {
                    val index = it.columnAliasList.indexOf(this)
                    it.selectStatement().queryExposed().flatMap { it.columns }.map { it.element }.get(index)
                }

                else -> throw IllegalStateException("Unexpected column alias parent $it")
            }
        }
    }

    private fun SqlCteTableName.selectStatement(): SqlCompoundSelectStmt {
        val withClause = parent as SqlWithClause
        return withClause.compoundSelectStmtList[withClause.cteTableNameList.indexOf(this)]
    }
}