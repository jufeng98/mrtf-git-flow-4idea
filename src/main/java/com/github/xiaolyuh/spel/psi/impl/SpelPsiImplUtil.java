package com.github.xiaolyuh.spel.psi.impl;

import com.github.xiaolyuh.spel.psi.SpelFieldName;
import com.github.xiaolyuh.spel.psi.SpelFieldOrMethodName;
import com.github.xiaolyuh.spel.psi.SpelMethodParam;
import com.github.xiaolyuh.spel.psi.SpelSpel;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import org.jetbrains.annotations.NotNull;

public class SpelPsiImplUtil {
    public static PsiReference @NotNull [] getReferences(SpelFieldName param) {
        return ReferenceProvidersRegistry.getReferencesFromProviders(param);
    }

    public static PsiReference @NotNull [] getReferences(SpelMethodParam param) {
        return ReferenceProvidersRegistry.getReferencesFromProviders(param);
    }

    public static PsiReference @NotNull [] getReferences(SpelFieldOrMethodName param) {
        return ReferenceProvidersRegistry.getReferencesFromProviders(param);
    }

    public static PsiReference @NotNull [] getReferences(SpelSpel param) {
        return ReferenceProvidersRegistry.getReferencesFromProviders(param);
    }
}
