package com.github.xiaolyuh.sql.psi.impl;

import com.github.xiaolyuh.sql.psi.SqlStatement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import org.jetbrains.annotations.NotNull;

public class SqlPsiImplUtil {

    public static PsiReference @NotNull [] getReferences(SqlStatement param) {
        return ReferenceProvidersRegistry.getReferencesFromProviders(param);
    }

}
