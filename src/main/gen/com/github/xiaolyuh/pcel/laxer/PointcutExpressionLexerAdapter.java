package com.github.xiaolyuh.pcel.laxer;

import com.intellij.lexer.FlexAdapter;

public class PointcutExpressionLexerAdapter extends FlexAdapter {

    public PointcutExpressionLexerAdapter() {
        super(new PointcutExpressionLexer(null));
    }

}