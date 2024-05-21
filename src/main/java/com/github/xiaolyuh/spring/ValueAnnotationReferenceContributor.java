package com.github.xiaolyuh.spring;

import com.google.common.collect.Sets;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class ValueAnnotationReferenceContributor extends PsiReferenceContributor {
    private static final Set<String> INTEREST_ANNO_SET = Sets.newHashSet(
            "org.springframework.beans.factory.annotation.Value",
            "com.ctrip.framework.apollo.spring.annotation.ApolloJsonValue"
    );
    private static final String DOLLAR_START = "${";
    private static final String DOLLAR_END = "}";

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(StandardPatterns.instanceOf(PsiLiteralExpression.class),
                new PsiReferenceProvider() {
                    @Override
                    public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element,
                                                                           @NotNull ProcessingContext context) {
                        PsiLiteralExpression psiLiteralExpression = (PsiLiteralExpression) element;
                        PsiAnnotation psiAnnotation = PsiTreeUtil.getParentOfType(element, PsiAnnotation.class);
                        if (psiAnnotation == null) {
                            return PsiReference.EMPTY_ARRAY;
                        }

                        String qualifiedName = psiAnnotation.getQualifiedName();
                        if (!INTEREST_ANNO_SET.contains(qualifiedName)) {
                            return PsiReference.EMPTY_ARRAY;
                        }

                        String value = (String) psiLiteralExpression.getValue();
                        if (value == null) {
                            return PsiReference.EMPTY_ARRAY;
                        }

                        int startIdx = value.indexOf(DOLLAR_START);
                        if (startIdx != -1) {
                            return handleDollarPlaceHolder(value, startIdx, psiLiteralExpression);
                        }

                        return PsiReference.EMPTY_ARRAY;
                    }
                });
    }

    private PsiReference[] handleDollarPlaceHolder(String value, int startIdx, PsiLiteralExpression psiLiteralExpression) {
        int endIdx;
        int separatorIndex = value.indexOf(":");
        if (separatorIndex != -1) {
            endIdx = separatorIndex + DOLLAR_END.length();
        } else {
            endIdx = value.indexOf(DOLLAR_END, startIdx) + DOLLAR_END.length();
        }

        TextRange textRange = new TextRange(startIdx + DOLLAR_START.length() + 1, endIdx);
        return new PsiReference[]{new ValueAnnotationReference(psiLiteralExpression, textRange)};
    }
}
