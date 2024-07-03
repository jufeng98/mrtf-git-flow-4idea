package com.github.xiaolyuh.spring;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.ResolveResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CommonReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {
    private final PsiElement targetPsiElement;

    public CommonReference(@NotNull PsiElement element, TextRange textRange, PsiElement targetPsiElement) {
        super(element, textRange);
        this.targetPsiElement = targetPsiElement;
    }

    @Override
    public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
        PsiElementResolveResult resolveResult = new PsiElementResolveResult(targetPsiElement);
        return new ResolveResult[]{resolveResult};
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        return resolveResults[0].getElement();
    }
}
