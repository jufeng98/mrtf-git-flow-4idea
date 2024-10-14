package com.github.xiaolyuh.http.reference;

import com.github.xiaolyuh.http.psi.HttpHeaders;
import com.github.xiaolyuh.http.psi.HttpUrl;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import org.jetbrains.annotations.NotNull;


public class HttpReferenceContributor extends PsiReferenceContributor {

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(HttpUrl.class), new HttpPsiReferenceProvider());

        HttpVariablePsiReferenceProvider provider = new HttpVariablePsiReferenceProvider();

        registrar.registerReferenceProvider(PlatformPatterns.psiElement(HttpUrl.class), provider);
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(HttpHeaders.class), provider);
    }

}