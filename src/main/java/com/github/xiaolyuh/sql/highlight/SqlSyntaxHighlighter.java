package com.github.xiaolyuh.sql.highlight;

import com.github.xiaolyuh.sql.parser.SqlAdapter;
import com.github.xiaolyuh.sql.psi.SqlTypes;
import com.github.xiaolyuh.utils.SqlUtils;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.xdebugger.ui.DebuggerColors;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;


public class SqlSyntaxHighlighter extends SyntaxHighlighterBase {
    private final Map<IElementType, TextAttributesKey> ourAttributes = new HashMap<>();

    {
        fillMap(ourAttributes, IDENTIFIER, SqlTypes.ID);
        fillMap(ourAttributes, DOT, SqlTypes.DOT);
        fillMap(ourAttributes, NUMBER, SqlTypes.DIGIT);
        fillMap(ourAttributes, STRING, SqlTypes.STRING);
        fillMap(ourAttributes, DebuggerColors.INLINED_VALUES_MODIFIED, SqlTypes.MYBATIS_OGNL);
        fillMap(ourAttributes, COMMENT, SqlTypes.COMMENT, SqlTypes.BLOCK_COMMENT);
        fillMap(ourAttributes, BAD_CHARACTER, TokenType.BAD_CHARACTER);

        for (IElementType sqlKeyword : SqlUtils.SQL_KEYWORDS) {
            ourAttributes.put(sqlKeyword, KEYWORD);
        }
    }

    public static final TextAttributesKey IDENTIFIER =
            createTextAttributesKey("SQL_IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER);
    public static final TextAttributesKey KEYWORD =
            createTextAttributesKey("SQL_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey STRING =
            createTextAttributesKey("SQL_STRING", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey NUMBER =
            createTextAttributesKey("SQL_NUMBER", DefaultLanguageHighlighterColors.NUMBER);
    public static final TextAttributesKey DOT =
            createTextAttributesKey("SQL_DOT", DefaultLanguageHighlighterColors.DOT);
    public static final TextAttributesKey COMMENT =
            createTextAttributesKey("SQL_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
    public static final TextAttributesKey BAD_CHARACTER =
            createTextAttributesKey("SQL_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER);


    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new SqlAdapter();
    }

    @Override
    public TextAttributesKey @NotNull [] getTokenHighlights(IElementType tokenType) {
        return pack(ourAttributes.get(tokenType));
    }

}