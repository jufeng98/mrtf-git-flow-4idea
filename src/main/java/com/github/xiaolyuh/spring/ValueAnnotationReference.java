package com.github.xiaolyuh.spring;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.ResolveResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ValueAnnotationReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {
    private final PsiElement targetPsiElement;

    public ValueAnnotationReference(@NotNull PsiElement element, TextRange textRange, PsiElement targetPsiElement) {
        super(element, textRange);
        this.targetPsiElement = targetPsiElement;
    }

    @Override
    public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
        return new ResolveResult[]{new PsiElementResolveResult(targetPsiElement)};
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        return resolveResults[0].getElement();
    }

}
