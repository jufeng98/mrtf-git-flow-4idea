package com.github.xiaolyuh.navigation;

import com.github.xiaolyuh.listener.FilePsiReference;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiNewExpression;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class FilePsiReferenceProvider extends PsiReferenceProvider {
    @Override
    public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement psiElement,
                                                           @NotNull ProcessingContext processingContext) {
        PsiLiteralExpression psiLiteralExpression = (PsiLiteralExpression) psiElement;
        PsiNewExpression psiNewExpression = PsiTreeUtil.getParentOfType(psiElement, PsiNewExpression.class);
        if (psiNewExpression == null) {
            return PsiReference.EMPTY_ARRAY;
        }
        PsiJavaCodeReferenceElement classReference = psiNewExpression.getClassReference();
        if (classReference == null) {
            return PsiReference.EMPTY_ARRAY;
        }
        PsiClass psiClass = (PsiClass) classReference.resolve();
        if (psiClass == null) {
            return PsiReference.EMPTY_ARRAY;
        }
        String className = psiClass.getQualifiedName();
        if (!File.class.getName().equals(className)) {
            return PsiReference.EMPTY_ARRAY;
        }
        return new FilePsiReference[]{new FilePsiReference(psiLiteralExpression, true)};
    }
}
