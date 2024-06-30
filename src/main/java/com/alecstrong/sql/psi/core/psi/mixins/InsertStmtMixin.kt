package com.alecstrong.sql.psi.core.psi.mixins

import com.alecstrong.sql.psi.core.SqlAnnotationHolder
import com.github.xiaolyuh.sql.psi.SqlColumnDef
import com.github.xiaolyuh.sql.psi.SqlColumnName
import com.github.xiaolyuh.sql.psi.SqlInsertStmt
import com.github.xiaolyuh.sql.psi.SqlTypes
import com.intellij.lang.ASTNode

internal abstract class InsertStmtMixin(
    node: ASTNode
) : MutatorMixin(node),
    SqlInsertStmt {
    override fun annotate(annotationHolder: SqlAnnotationHolder) {
        val table = tableAvailable(this, tableName.name).firstOrNull() ?: return
        val columns = table.columns.map { (it.element as SqlColumnName).name }
        val setColumns =
            if (columnNameList.isEmpty() && node.findChildByType(SqlTypes.DEFAULT) == null) {
                columns
            } else {
                columnNameList.mapNotNull { it.name }
            }

        valuesExpressionList.forEach {
            if (it.exprList.size != setColumns.size) {
                annotationHolder.createErrorAnnotation(
                    it, "Unexpected number of values being inserted." +
                            " found: ${it.exprList.size} expected: ${setColumns.size}"
                )
            }
        }

        compoundSelectStmt?.let { select ->
            val size = select.queryExposed().flatMap { it.columns }.count()
            if (size != setColumns.size) {
                annotationHolder.createErrorAnnotation(
                    select, "Unexpected number of values being" +
                            " inserted. found: $size expected: ${setColumns.size}"
                )
            }
        }

        val needsDefaultValue = table.columns
            .filterNot { (element, _) -> element is SqlColumnName && element.name in setColumns }
            .map { it.element as SqlColumnName }
            .filterNot { (it.parent as SqlColumnDef).hasDefaultValue() }
        if (needsDefaultValue.size == 1) {
            annotationHolder.createErrorAnnotation(
                this, "Cannot populate default value for column " +
                        "${needsDefaultValue.first().name}, it must be specified in insert statement."
            )
        } else if (needsDefaultValue.size > 1) {
            annotationHolder.createErrorAnnotation(
                this, "Cannot populate default values for columns " +
                        "(${needsDefaultValue.joinToString { it.name }}), they must be specified in insert statement."
            )
        }

        super.annotate(annotationHolder)
    }

    protected fun SqlColumnDef.hasDefaultValue(): Boolean {
        return columnConstraintList.any {
            it.node.findChildByType(SqlTypes.DEFAULT) != null
                    || it.node.findChildByType(SqlTypes.AUTOINCREMENT) != null
        } || columnConstraintList.none {
            it.node.findChildByType(SqlTypes.NOT) != null
        } || (
                // An INTEGER PRIMARY KEY is still considered to have a default value, even without specifying AUTOINCREMENT:
                // https://www.sql.org/autoinc.html
                // "On an INSERT, if the ROWID or INTEGER PRIMARY KEY column is not explicitly given a value, then it will be
                // filled automatically with an unused integer .. regardless of whether or not the AUTOINCREMENT keyword is used."
                this.typeName.text == "INTEGER" && this.columnConstraintList.any {
                    it.node.findChildByType(SqlTypes.PRIMARY) != null
                }
                )
    }
}