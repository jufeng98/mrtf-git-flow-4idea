package com.github.xiaolyuh.spring;

import com.github.xiaolyuh.utils.ValueUtils;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.psi.ResolveResult;
import com.intellij.util.ProcessingContext;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class ValueAnnotationReferenceContributor extends PsiReferenceContributor {

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(StandardPatterns.instanceOf(PsiLiteralExpression.class),
                new PsiReferenceProvider() {
                    @Override
                    public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element,
                                                                           @NotNull ProcessingContext context) {
                        Triple<String, TextRange, List<PsiElement>> triple = ValueUtils.findApolloConfig(element);
                        if (triple == null) {
                            return PsiReference.EMPTY_ARRAY;
                        }

                        TextRange textRange = triple.getMiddle();
                        List<PsiElement> psiElements = triple.getRight();

                        return new PsiReference[]{new ValueAnnotationReference(element, textRange, psiElements)};
                    }
                });
    }

    public static final class ValueAnnotationReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {
        private final List<PsiElement> targetPsiElements;

        public ValueAnnotationReference(@NotNull PsiElement element, TextRange textRange, List<PsiElement> targetPsiElements) {
            super(element, textRange);
            this.targetPsiElements = targetPsiElements;
        }

        @Override
        public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
            return targetPsiElements.stream()
                    .map(PsiElementResolveResult::new)
                    .toArray(ResolveResult[]::new);
        }

        @Nullable
        @Override
        public PsiElement resolve() {
            ResolveResult[] resolveResults = multiResolve(false);
            return resolveResults[0].getElement();
        }

    }
}
