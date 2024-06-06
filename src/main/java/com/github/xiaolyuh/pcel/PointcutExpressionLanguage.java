package com.github.xiaolyuh.pcel;

import com.intellij.lang.Language;

public class PointcutExpressionLanguage extends Language {

    public static final PointcutExpressionLanguage INSTANCE = new PointcutExpressionLanguage();

    private PointcutExpressionLanguage() {
        super("PointcutExpression");
    }

}
