package com.github.xiaolyuh.http.psi.impl;

import com.github.xiaolyuh.http.psi.HttpUrl;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import org.jetbrains.annotations.NotNull;

public class HttpPsiImplUtil {

    public static PsiReference @NotNull [] getReferences(HttpUrl param) {
        return ReferenceProvidersRegistry.getReferencesFromProviders(param);
    }

}
