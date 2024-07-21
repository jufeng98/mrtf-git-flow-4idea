package com.github.xiaolyuh.sql.psi.impl;

import com.github.xiaolyuh.sql.psi.SqlNamedElement;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public abstract class SqlNamedElementImpl extends ASTWrapperPsiElement implements SqlNamedElement {

    public SqlNamedElementImpl(@NotNull ASTNode node) {
        super(node);
    }

}
