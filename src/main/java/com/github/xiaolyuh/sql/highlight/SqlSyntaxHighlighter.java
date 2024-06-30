package com.github.xiaolyuh.sql.highlight;

import com.github.xiaolyuh.sql.parser.SqlAdapter;
import com.github.xiaolyuh.sql.psi.SqlTypes;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;


public class SqlSyntaxHighlighter extends SyntaxHighlighterBase {

    public static final TextAttributesKey IDENTIFIER =
            createTextAttributesKey("IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER);
    public static final TextAttributesKey KEYWORD =
            createTextAttributesKey("KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey STRING =
            createTextAttributesKey("STRING", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey NUMBER =
            createTextAttributesKey("NUMBER", DefaultLanguageHighlighterColors.NUMBER);
    public static final TextAttributesKey DOT =
            createTextAttributesKey("DOT", DefaultLanguageHighlighterColors.DOT);
    public static final TextAttributesKey BAD_CHARACTER =
            createTextAttributesKey("BAD_CHARACTER", HighlighterColors.BAD_CHARACTER);


    private static final TextAttributesKey[] IDENTIFIER_KEYS = new TextAttributesKey[]{IDENTIFIER};
    private static final TextAttributesKey[] KEYWORD_KEYS = new TextAttributesKey[]{KEYWORD};
    private static final TextAttributesKey[] STRING_KEYS = new TextAttributesKey[]{STRING};
    private static final TextAttributesKey[] NUMBER_KEYS = new TextAttributesKey[]{NUMBER};
    private static final TextAttributesKey[] DOT_KEYS = new TextAttributesKey[]{DOT};
    private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{BAD_CHARACTER};
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new SqlAdapter();
    }

    @Override
    public TextAttributesKey @NotNull [] getTokenHighlights(IElementType tokenType) {
        if (tokenType.equals(SqlTypes.SELECT)
                || tokenType.equals(SqlTypes.DELETE)
                || tokenType.equals(SqlTypes.ADD)
                || tokenType.equals(SqlTypes.UPDATE)
                || tokenType.equals(SqlTypes.FROM)
                || tokenType.equals(SqlTypes.INNER)
                || tokenType.equals(SqlTypes.LEFT)
                || tokenType.equals(SqlTypes.JOIN)
                || tokenType.equals(SqlTypes.WHEN)
                || tokenType.equals(SqlTypes.WHERE)
                || tokenType.equals(SqlTypes.CASE)
                || tokenType.equals(SqlTypes.IF)
                || tokenType.equals(SqlTypes.AS)
                || tokenType.equals(SqlTypes.ON)
                || tokenType.equals(SqlTypes.AND)
                || tokenType.equals(SqlTypes.IS)
                || tokenType.equals(SqlTypes.NOT)
                || tokenType.equals(SqlTypes.NULL)
                || tokenType.equals(SqlTypes.CREATE)
                || tokenType.equals(SqlTypes.EXISTS)
                || tokenType.equals(SqlTypes.NO)
                || tokenType.equals(SqlTypes.END)
                || tokenType.equals(SqlTypes.FOR)
                || tokenType.equals(SqlTypes.OR)
                || tokenType.equals(SqlTypes.COLUMN)
                || tokenType.equals(SqlTypes.LIKE)
                || tokenType.equals(SqlTypes.ELSE)
                || tokenType.equals(SqlTypes.IN)
                || tokenType.equals(SqlTypes.TO)
                || tokenType.equals(SqlTypes.CAST)
                || tokenType.equals(SqlTypes.LIMIT)
                || tokenType.equals(SqlTypes.OFFSET)
                || tokenType.equals(SqlTypes.OF)
                || tokenType.equals(SqlTypes.TABLE)
                || tokenType.equals(SqlTypes.INDEX)
                || tokenType.equals(SqlTypes.ASC)
                || tokenType.equals(SqlTypes.DESC)
                || tokenType.equals(SqlTypes.BETWEEN)
                || tokenType.equals(SqlTypes.BY)
                || tokenType.equals(SqlTypes.ORDER)
                || tokenType.equals(SqlTypes.VALUES)
                || tokenType.equals(SqlTypes.UNIQUE)
                || tokenType.equals(SqlTypes.UNION)
                || tokenType.equals(SqlTypes.TRUE)
                || tokenType.equals(SqlTypes.FALSE)
                || tokenType.equals(SqlTypes.SET)
                || tokenType.equals(SqlTypes.PRIMARY)
                || tokenType.equals(SqlTypes.KEY)
                || tokenType.equals(SqlTypes.HAVING)
                || tokenType.equals(SqlTypes.GROUP)) {
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

        if (tokenType.equals(TokenType.BAD_CHARACTER)) {
            return BAD_CHAR_KEYS;
        }

        return EMPTY_KEYS;
    }

}