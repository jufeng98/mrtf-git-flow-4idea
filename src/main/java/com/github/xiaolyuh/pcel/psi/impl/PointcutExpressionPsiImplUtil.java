package com.github.xiaolyuh.pcel.psi.impl;

import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import org.jetbrains.annotations.NotNull;

public class PointcutExpressionPsiImplUtil {

    public static PsiReference @NotNull [] getReferences(AopExprImpl aopExpr) {
        return ReferenceProvidersRegistry.getReferencesFromProviders(aopExpr);
    }

    public static PsiReference @NotNull [] getReferences(AopMethodImpl aopExpr) {
        return ReferenceProvidersRegistry.getReferencesFromProviders(aopExpr);
    }
}
