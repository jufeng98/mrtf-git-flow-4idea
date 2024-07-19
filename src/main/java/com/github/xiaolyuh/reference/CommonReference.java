package com.github.xiaolyuh.reference;

import com.github.xiaolyuh.utils.TooltipUtils;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
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

        if (targetPsiElement == null) {
            targetPsiElement = new FakePsiElement(element);
        }

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

    public static class FakePsiElement extends ASTWrapperPsiElement {
        private final PsiElement element;

        public FakePsiElement(PsiElement element) {
            super(element.getNode());
            this.element = element;
        }

        @Override
        public void navigate(boolean requestFocus) {
            TooltipUtils.showTooltip("无法找到要跳转的声明", element.getProject());
        }
    }
}
