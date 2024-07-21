package com.github.xiaolyuh.sql.reference;

import com.github.xiaolyuh.sql.psi.impl.FakePsiElement;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ColumnTableAliasPsiReference extends PsiReferenceBase<PsiElement> {
    private final PsiElement targetPsiElement;

    public ColumnTableAliasPsiReference(@NotNull PsiElement element, @NotNull TextRange textRange, PsiElement targetPsiElement) {
        super(element, textRange);

        if (targetPsiElement == null) {
            targetPsiElement = new FakePsiElement(element);
        }

        this.targetPsiElement = targetPsiElement;
    }


    @Nullable
    @Override
    public PsiElement resolve() {
        return targetPsiElement;
    }

}
