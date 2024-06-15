package com.github.xiaolyuh.spel.highlight;

import com.github.xiaolyuh.spel.parser.SpelAdapter;
import com.github.xiaolyuh.spel.psi.SpelTypes;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

/**
 * 第十三步:定义语法高亮器
 */
public class SpelSyntaxHighlighter extends SyntaxHighlighterBase {

    public static final TextAttributesKey IDENTIFIER =
            createTextAttributesKey("IDENTIFIER", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey STRING =
            createTextAttributesKey("STRING", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey NUMBER =
            createTextAttributesKey("NUMBER", DefaultLanguageHighlighterColors.NUMBER);
    public static final TextAttributesKey BAD_CHARACTER =
            createTextAttributesKey("SIMPLE_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER);


    private static final TextAttributesKey[] IDENTIFIER_KEYS = new TextAttributesKey[]{IDENTIFIER};
    private static final TextAttributesKey[] STRING_KEYS = new TextAttributesKey[]{STRING};
    private static final TextAttributesKey[] NUMBER_KEYS = new TextAttributesKey[]{NUMBER};
    private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{BAD_CHARACTER};
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new SpelAdapter();
    }

    @Override
    public TextAttributesKey @NotNull [] getTokenHighlights(IElementType tokenType) {
        if (tokenType.equals(SpelTypes.NUMBER_LITERAL)) {
            return NUMBER_KEYS;
        }
        if (tokenType.equals(SpelTypes.STRING_LITERAL)) {
            return STRING_KEYS;
        }
        if (tokenType.equals(SpelTypes.FIELD_NAME)) {
            return IDENTIFIER_KEYS;
        }
        if (tokenType.equals(SpelTypes.FIELD_OR_METHOD_NAME)) {
            return IDENTIFIER_KEYS;
        }
        if (tokenType.equals(SpelTypes.IDENTIFIER)) {
            return IDENTIFIER_KEYS;
        }
        if (tokenType.equals(SpelTypes.METHOD_PARAM)) {
            return IDENTIFIER_KEYS;
        }

        if (tokenType.equals(TokenType.BAD_CHARACTER)) {
            return BAD_CHAR_KEYS;
        }

        return EMPTY_KEYS;
    }

}