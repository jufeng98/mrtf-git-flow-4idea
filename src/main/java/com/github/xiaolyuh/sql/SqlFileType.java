package com.github.xiaolyuh.sql;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class SqlFileType extends LanguageFileType {

    public static final SqlFileType INSTANCE = new SqlFileType();

    private SqlFileType() {
        super(SqlLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "SQL";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "SQL language file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "sql";
    }

    @Override
    public Icon getIcon() {
        return SqlIcons.FILE;
    }

}
