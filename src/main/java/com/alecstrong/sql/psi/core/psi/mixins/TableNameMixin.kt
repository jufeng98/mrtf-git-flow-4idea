package com.alecstrong.sql.psi.core.psi.mixins

import com.alecstrong.sql.psi.core.SqlAnnotationHolder
import com.alecstrong.sql.psi.core.psi.SqlNamedElementImpl
import com.alecstrong.sql.psi.core.psi.SqlTableReference
import com.github.xiaolyuh.sql.parser.SqlParser
import com.github.xiaolyuh.sql.psi.SqlColumnExpr
import com.github.xiaolyuh.sql.psi.SqlForeignTable
import com.github.xiaolyuh.sql.psi.SqlTableName
import com.github.xiaolyuh.sql.psi.SqlViewName
import com.intellij.lang.ASTNode
import com.intellij.lang.PsiBuilder
import com.intellij.psi.PsiReference

internal abstract class TableNameMixin(
    node: ASTNode
) : SqlNamedElementImpl(node) {
    override val parseRule: (PsiBuilder, Int) -> Boolean
        get() = when (this) {
            is SqlTableName -> SqlParser::table_name
            is SqlViewName -> SqlParser::view_name
            is SqlForeignTable -> SqlParser::foreign_table
            else -> throw IllegalStateException("Unknown table type ${this::class}")
        }

    override fun getReference(): PsiReference {
        return SqlTableReference(this)
    }

    override fun annotate(annotationHolder: SqlAnnotationHolder) {
        // Handled by ColumnNameMixin
        if (parent is SqlColumnExpr) return

        val matches by lazy { tableAvailable(this, name) }
        val references = reference.resolve()
        if (references == this) {
            if (matches.any { it.table != this }) {
                annotationHolder.createErrorAnnotation(this, "Table already defined with name $name")
            }
        } else if (references == null) {
            annotationHolder.createErrorAnnotation(this, "No table found with name $name")
        }
        super.annotate(annotationHolder)
    }
}