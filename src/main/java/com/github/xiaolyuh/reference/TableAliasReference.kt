package com.github.xiaolyuh.reference

import com.github.xiaolyuh.sql.psi.SqlStatement
import com.github.xiaolyuh.sql.psi.SqlTableAlias
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*

class TableAliasReference(
    sqlStatement: SqlStatement,
    private val sqlTableAlias: SqlTableAlias,
    textRange: TextRange,
) :
    PsiReferenceBase<PsiElement>(sqlStatement, textRange), PsiPolyVariantReference {

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        val ele = object : ASTWrapperPsiElement(sqlTableAlias.node) {
            override fun navigate(requestFocus: Boolean) {
                // TODO
            }
        }
        return arrayOf(PsiElementResolveResult(ele))
    }

    override fun resolve(): PsiElement? {
        if (sqlTableAlias.references.isEmpty()) {
            return null
        }

        return sqlTableAlias.references[0].resolve()
    }

}
