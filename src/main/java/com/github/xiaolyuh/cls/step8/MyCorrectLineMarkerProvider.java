package com.github.xiaolyuh.cls.step8;

import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

final class MyCorrectLineMarkerProvider implements LineMarkerProvider {
    public LineMarkerInfo<PsiElement> getLineMarkerInfo(@NotNull PsiElement element) {
        if (element instanceof PsiIdentifier &&
                element.getParent() instanceof PsiMethod) {
            return new LineMarkerInfo<>(element, element.getTextRange());
        }
        return null;
    }
}