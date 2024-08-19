package com.github.xiaolyuh.http.highlight

import com.github.xiaolyuh.http.parser.HttpAdapter
import com.github.xiaolyuh.http.psi.HttpTypes
import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType

class HttpSyntaxHighlighter : SyntaxHighlighterBase() {

    override fun getHighlightingLexer(): Lexer {
        return HttpAdapter()
    }

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> {
        return when (tokenType) {
            HttpTypes.POST -> arrayOf(KEYWORD)
            HttpTypes.GET -> arrayOf(KEYWORD)
            HttpTypes.PUT -> arrayOf(KEYWORD)
            HttpTypes.DELETE -> arrayOf(KEYWORD)
            HttpTypes.URL_DESC -> arrayOf(STRING)
            HttpTypes.HEADER_DESC -> arrayOf(NUMBER)
            HttpTypes.JSON_TEXT -> arrayOf(IDENTIFIER)
            HttpTypes.XML_TEXT -> arrayOf(IDENTIFIER)
            HttpTypes.FILE -> arrayOf(IDENTIFIER)
            HttpTypes.URL_FORM_ENCODE -> arrayOf(IDENTIFIER)
            TokenType.BAD_CHARACTER -> arrayOf(BAD_CHARACTER)
            else -> arrayOf()
        }
    }

    companion object {
        val NUMBER: TextAttributesKey =
            TextAttributesKey.createTextAttributesKey("SQL_NUMBER", DefaultLanguageHighlighterColors.NUMBER)
        val IDENTIFIER: TextAttributesKey =
            TextAttributesKey.createTextAttributesKey("HTTP_IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER)
        val STRING: TextAttributesKey =
            TextAttributesKey.createTextAttributesKey("HTTP_STRING", DefaultLanguageHighlighterColors.STRING)
        val COMMENT: TextAttributesKey =
            TextAttributesKey.createTextAttributesKey("HTTP_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)
        val BAD_CHARACTER: TextAttributesKey =
            TextAttributesKey.createTextAttributesKey("HTTP_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER)
        val KEYWORD: TextAttributesKey =
            TextAttributesKey.createTextAttributesKey("HTTP_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)
    }
}