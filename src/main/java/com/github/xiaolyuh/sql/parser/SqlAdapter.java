package com.github.xiaolyuh.sql.parser;

import com.github.xiaolyuh.sql._SqlLexer;
import com.intellij.lexer.FlexAdapter;

public class SqlAdapter extends FlexAdapter {

    public SqlAdapter() {
        super(new _SqlLexer(null));
    }

}
