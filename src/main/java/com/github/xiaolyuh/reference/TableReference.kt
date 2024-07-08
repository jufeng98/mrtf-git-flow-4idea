package com.github.xiaolyuh.reference

import com.github.xiaolyuh.dbn.DbnToolWindowPsiElement
import com.github.xiaolyuh.sql.psi.SqlStatement
import com.github.xiaolyuh.sql.psi.SqlTableName
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.ResolveResult

class TableReference(
    sqlStatement: SqlStatement,
    private val sqlTableName: SqlTableName,
    textRange: TextRange,
) :
    PsiReferenceBase<PsiElement>(sqlStatement, textRange), PsiPolyVariantReference {
    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult?> {
        return arrayOfNulls(0)
    }

    override fun resolve(): PsiElement {
        return DbnToolWindowPsiElement(sqlTableName.text, null, sqlTableName.node)
    }
}
