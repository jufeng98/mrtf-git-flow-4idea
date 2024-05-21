package com.github.xiaolyuh.spring;

import com.github.xiaolyuh.utils.ValueUtils;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.util.ProcessingContext;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;


public class ValueAnnotationReferenceContributor extends PsiReferenceContributor {


    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(StandardPatterns.instanceOf(PsiLiteralExpression.class),
                new PsiReferenceProvider() {
                    @Override
                    public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element,
                                                                           @NotNull ProcessingContext context) {
                        Triple<String, TextRange, PsiElement> triple = ValueUtils.findApolloConfig(element);
                        if (triple == null) {
                            return PsiReference.EMPTY_ARRAY;
                        }

                        TextRange textRange = triple.getMiddle();
                        PsiElement psiElement = triple.getRight();

                        return new PsiReference[]{new ValueAnnotationReference(element, textRange, psiElement)};
                    }
                });
    }

}
