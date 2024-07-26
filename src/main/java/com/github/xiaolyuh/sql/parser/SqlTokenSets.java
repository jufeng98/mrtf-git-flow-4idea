package com.github.xiaolyuh.sql.parser;

import com.github.xiaolyuh.sql.psi.SqlTypes;
import com.intellij.psi.tree.TokenSet;


public class SqlTokenSets {
    public static final TokenSet STRING_LITERALS = TokenSet.create(SqlTypes.STRING);

    public static final TokenSet SQL_COMMENT = TokenSet.create(SqlTypes.COMMENT);
}
