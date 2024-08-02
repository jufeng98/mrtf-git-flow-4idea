package com.github.xiaolyuh.sql.psi.impl;

import com.dbn.language.sql.SqlElementFactory;
import com.github.xiaolyuh.sql.psi.SqlColumnAlias;
import com.github.xiaolyuh.sql.psi.SqlColumnName;
import com.github.xiaolyuh.sql.psi.SqlStatement;
import com.github.xiaolyuh.sql.psi.SqlTableAlias;
import com.github.xiaolyuh.sql.psi.SqlTableName;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

public class SqlPsiImplUtil {
    public static final String ANTI_QUOTE_CHAR = "`";

    public static PsiElement setName(SqlTableAlias element, String newName) {
        PsiElement psiElement = SqlElementFactory.createSqlElement(element.getProject(), "select * from table " + newName);
        return PsiTreeUtil.findChildOfType(psiElement, SqlTableAlias.class);
    }

    public static PsiElement setName(SqlColumnAlias element, String newName) {
        PsiElement psiElement = SqlElementFactory.createSqlElement(element.getProject(), "select id as " + newName + " from table");
        return PsiTreeUtil.findChildOfType(psiElement, SqlColumnAlias.class);
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
