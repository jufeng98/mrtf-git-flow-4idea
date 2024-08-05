package com.github.xiaolyuh.sql.psi.impl;

import com.github.xiaolyuh.sql.SqlElementFactory;
import com.github.xiaolyuh.sql.psi.SqlColumnAlias;
import com.github.xiaolyuh.sql.psi.SqlColumnName;
import com.github.xiaolyuh.sql.psi.SqlStatement;
import com.github.xiaolyuh.sql.psi.SqlTableAlias;
import com.github.xiaolyuh.sql.psi.SqlTableName;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import org.jetbrains.annotations.NotNull;

public class SqlPsiImplUtil {
    public static final String ANTI_QUOTE_CHAR = "`";

    public static PsiElement setName(SqlTableAlias element, String newName) {
        SqlTableAlias sqlTableAlias = SqlElementFactory.INSTANCE.createSqlTableAlias(element.getProject(), newName);
        element.replace(sqlTableAlias);
        return sqlTableAlias;
    }

    public static PsiElement setName(SqlColumnAlias element, String newName) {
        SqlColumnAlias sqlColumnAlias = SqlElementFactory.INSTANCE.createSqlColumnAlias(element.getProject(), newName);
        element.replace(sqlColumnAlias);
        return sqlColumnAlias;
    }

    public static String getName(SqlTableAlias element) {
        return element.getText().replace(ANTI_QUOTE_CHAR, "");
    }

    public static String getName(SqlColumnAlias element) {
        return element.getText();
    }

    public static String getName(SqlTableName element) {
        return element.getText().replace(ANTI_QUOTE_CHAR, "");
    }

    public static String getName(SqlColumnName element) {
        return element.getText().replace(ANTI_QUOTE_CHAR, "");
    }

    public static PsiElement getNameIdentifier(SqlTableAlias element) {
        return element;
    }

    public static PsiElement getNameIdentifier(SqlColumnAlias element) {
        return element;
    }

    public static PsiReference @NotNull [] getReferences(SqlStatement param) {
        return ReferenceProvidersRegistry.getReferencesFromProviders(param);
    }

}
