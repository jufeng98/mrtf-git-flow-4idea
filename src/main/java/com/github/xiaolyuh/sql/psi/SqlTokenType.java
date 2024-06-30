package com.github.xiaolyuh.sql.psi;

import com.github.xiaolyuh.sql.SqlLanguage;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class SqlTokenType extends IElementType {

    public SqlTokenType(@NotNull @NonNls String debugName) {
        super(debugName, SqlLanguage.INSTANCE);
    }

    @Override
    public String toString() {
        return SqlTokenType.class.getSimpleName() + "." + super.toString();
    }

}
