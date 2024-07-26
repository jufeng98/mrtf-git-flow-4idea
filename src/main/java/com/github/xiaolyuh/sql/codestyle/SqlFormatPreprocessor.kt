package com.github.xiaolyuh.sql.codestyle

import com.github.xiaolyuh.sql.SqlLanguage
import com.github.xiaolyuh.utils.SqlUtils
import com.intellij.application.options.CodeStyle
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.impl.source.codeStyle.PostFormatProcessorHelper
import com.intellij.psi.impl.source.codeStyle.PreFormatProcessor
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.DocumentUtil

class SqlFormatPreprocessor : PreFormatProcessor {
    override fun process(node: ASTNode, range: TextRange): TextRange {
        val psiElement = node.psi
        if (psiElement == null || !psiElement.isValid || !psiElement.language.isKindOf(SqlLanguage.INSTANCE)) return range

        val file = psiElement.containingFile
        val rootSettings: CodeStyleSettings = CodeStyle.getSettings(file)
        val customSettings = rootSettings.getCustomSettings(
            SqlCodeStyleSettings::class.java
        )

        handleCase(psiElement, customSettings)

        val postFormatProcessorHelper = PostFormatProcessorHelper(rootSettings.getCommonSettings(SqlLanguage.INSTANCE))
        postFormatProcessorHelper.resultTextRange = range

        val converter = SqlAntiQuotesConverter(psiElement, postFormatProcessorHelper)
        val document = converter.getDocument()
        if (document != null) {
            DocumentUtil.executeInBulk(document, converter)
        }

        return postFormatProcessorHelper.resultTextRange
    }

    private fun handleCase(psiElement: PsiElement, customSettings: SqlCodeStyleSettings) {
        val sqlCaseStyle = SqlCaseStyle.getByCode(customSettings.keywordCase)
        if (sqlCaseStyle === SqlCaseStyle.NOT_CHANGE) {
            return
        }

        val children = PsiTreeUtil.findChildrenOfType(psiElement, LeafPsiElement::class.java)
        children.forEach {
            if (!SqlUtils.isKeyword(it.elementType)) {
                return@forEach
            }

            sqlCaseStyle.doModifyKeyword(it)
        }
    }

}
