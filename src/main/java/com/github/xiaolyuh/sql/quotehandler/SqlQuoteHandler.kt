package com.github.xiaolyuh.sql.quotehandler

import com.github.xiaolyuh.sql.parser.SqlTokenSets
import com.intellij.codeInsight.editorActions.MultiCharQuoteHandler
import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.highlighter.HighlighterIterator
import com.intellij.psi.PsiFile

/**
 * 对于字符串类型,自动插入右单引号或右双引号
 */
class SqlQuoteHandler : SimpleTokenSetQuoteHandler(SqlTokenSets.STRING_LITERALS), MultiCharQuoteHandler {

    override fun getClosingQuote(iterator: HighlighterIterator, offset: Int): CharSequence {
        return iterator.document.text[offset - 1].toString()
    }

    override fun insertClosingQuote(editor: Editor, offset: Int, file: PsiFile, closingQuote: CharSequence) {
        editor.document.insertString(offset, closingQuote)
    }

}
