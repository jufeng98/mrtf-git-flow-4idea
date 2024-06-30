package com.github.xiaolyuh.sql.psi;

import com.github.xiaolyuh.sql.SqlLanguage;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class SqlElementType extends IElementType {

    public SqlElementType(@NotNull @NonNls String debugName) {
        super(debugName, SqlLanguage.INSTANCE);
    }

}
