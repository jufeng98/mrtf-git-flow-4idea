package com.github.xiaolyuh.sql.reference;

import com.github.xiaolyuh.sql.psi.impl.FakePsiElement;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.ResolveResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ColumnTableAliasPsiReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {
    private final PsiElement targetPsiElement;

    public ColumnTableAliasPsiReference(@NotNull PsiElement element, @NotNull TextRange textRange, PsiElement targetPsiElement) {
        super(element, textRange);

        if (targetPsiElement == null) {
            targetPsiElement = new FakePsiElement(element);
        }

        this.targetPsiElement = targetPsiElement;
    }

    @Override
    public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
        return new ResolveResult[]{new PsiElementResolveResult(targetPsiElement)};
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(true);
        return resolveResults.length == 1 ? resolveResults[0].getElement() : null;
    }

}
