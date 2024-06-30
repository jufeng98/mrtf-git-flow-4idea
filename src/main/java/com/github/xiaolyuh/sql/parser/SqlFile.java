package com.github.xiaolyuh.sql.parser;

import com.alecstrong.sql.psi.core.SqlFileBase;
import com.github.xiaolyuh.sql.SqlFileType;
import com.github.xiaolyuh.sql.SqlLanguage;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;

public class SqlFile extends SqlFileBase {

    public SqlFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, SqlLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return SqlFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "SQL File";
    }

}
