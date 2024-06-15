package com.github.xiaolyuh.spel.parser;

import com.github.xiaolyuh.spel._SpelLexer;
import com.intellij.lexer.FlexAdapter;

/**
 * 第十步:定义词法分析器适配器
 */
public class SpelAdapter extends FlexAdapter {

    public SpelAdapter() {
        super(new _SpelLexer(null));
    }

}
