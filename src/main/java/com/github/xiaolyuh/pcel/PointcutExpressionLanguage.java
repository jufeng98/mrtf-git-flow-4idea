package com.github.xiaolyuh.pcel;

import com.intellij.lang.Language;

/**
 * 第一步:定义语言
 */
public class PointcutExpressionLanguage extends Language {

    public static final PointcutExpressionLanguage INSTANCE = new PointcutExpressionLanguage();

    private PointcutExpressionLanguage() {
        super("PointcutExpression");
    }

}
