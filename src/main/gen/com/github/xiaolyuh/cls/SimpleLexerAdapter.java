package com.github.xiaolyuh.cls;

import com.intellij.lexer.FlexAdapter;

public class SimpleLexerAdapter extends FlexAdapter {

    public SimpleLexerAdapter() {
        super(new SimpleLexer(null));
    }

}
