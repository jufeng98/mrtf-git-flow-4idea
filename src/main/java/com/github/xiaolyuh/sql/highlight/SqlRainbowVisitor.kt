package com.github.xiaolyuh.sql.highlight

import com.github.xiaolyuh.sql.parser.SqlFile
import com.github.xiaolyuh.sql.psi.SqlColumnAlias
import com.github.xiaolyuh.sql.psi.SqlColumnName
import com.github.xiaolyuh.sql.psi.SqlFunctionName
import com.github.xiaolyuh.sql.psi.SqlTableName
import com.intellij.codeInsight.daemon.RainbowVisitor
import com.intellij.codeInsight.daemon.impl.HighlightVisitor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile

class SqlRainbowVisitor : RainbowVisitor() {
    override fun suitableForFile(file: PsiFile): Boolean {
        return file is SqlFile
    }

    override fun visit(element: PsiElement) {
        when (element) {
            is SqlColumnName -> {
                addInfo(element)
            }

            is SqlColumnAlias -> {
                addInfo(element)
            }

            is SqlTableName -> {
                addInfo(element)
            }

            is SqlFunctionName -> {
                addInfo(element)
            }
        }
    }

    private fun addInfo(element: PsiElement) {
        addInfo(
            getInfo(
                element.containingFile,
                element,
                element.javaClass.name,
                null
            )
        )
    }

    override fun clone(): HighlightVisitor {
        return SqlRainbowVisitor()
    }

}
