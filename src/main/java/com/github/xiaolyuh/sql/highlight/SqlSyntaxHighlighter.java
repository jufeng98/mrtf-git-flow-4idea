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

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;


public class SqlSyntaxHighlighter extends SyntaxHighlighterBase {

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


    private static final TextAttributesKey[] IDENTIFIER_KEYS = new TextAttributesKey[]{IDENTIFIER};
    private static final TextAttributesKey[] KEYWORD_KEYS = new TextAttributesKey[]{KEYWORD};
    private static final TextAttributesKey[] STRING_KEYS = new TextAttributesKey[]{STRING};
    private static final TextAttributesKey[] NUMBER_KEYS = new TextAttributesKey[]{NUMBER};
    private static final TextAttributesKey[] DOT_KEYS = new TextAttributesKey[]{DOT};
    private static final TextAttributesKey[] OGNL = new TextAttributesKey[]{DebuggerColors.INLINED_VALUES_MODIFIED};
    private static final TextAttributesKey[] COMMENT_KEYS = new TextAttributesKey[]{COMMENT};
    private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{BAD_CHARACTER};
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new SqlAdapter();
    }

    @Override
    public TextAttributesKey @NotNull [] getTokenHighlights(IElementType tokenType) {
        if (SqlUtils.isKeyword(tokenType)) {
            return KEYWORD_KEYS;
        }

        if (tokenType.equals(SqlTypes.ID)) {
            return IDENTIFIER_KEYS;
        }

        if (tokenType.equals(SqlTypes.DOT)) {
            return DOT_KEYS;
        }

        if (tokenType.equals(SqlTypes.DIGIT)) {
            return NUMBER_KEYS;
        }

        if (tokenType.equals(SqlTypes.STRING)) {
            return STRING_KEYS;
        }

        if (tokenType.equals(SqlTypes.MYBATIS_OGNL)) {
            return OGNL;
        }

        if (tokenType.equals(SqlTypes.COMMENT)) {
            return COMMENT_KEYS;
        }

        if (tokenType.equals(TokenType.BAD_CHARACTER)) {
            return BAD_CHAR_KEYS;
        }

        return EMPTY_KEYS;
    }

}