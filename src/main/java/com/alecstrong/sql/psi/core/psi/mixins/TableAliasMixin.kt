package com.alecstrong.sql.psi.core.psi.mixins

import com.alecstrong.sql.psi.core.psi.SqlNamedElementImpl
import com.github.xiaolyuh.sql.parser.SqlParser
import com.github.xiaolyuh.sql.psi.SqlTableAlias
import com.github.xiaolyuh.sql.psi.SqlTableOrSubquery
import com.intellij.lang.ASTNode
import com.intellij.lang.PsiBuilder
import com.intellij.psi.PsiElement

internal abstract class TableAliasMixin(
    node: ASTNode
) : SqlNamedElementImpl(node),
    SqlTableAlias {
    override val parseRule: (PsiBuilder, Int) -> Boolean = SqlParser::table_alias

    override fun source(): PsiElement {
        return (parent as SqlTableOrSubquery).let { it.tableName ?: it.compoundSelectStmt!! }
    }
}