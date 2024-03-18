package com.github.xiaolyuh.cls.step10;

import com.intellij.json.psi.JsonStringLiteral;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import static com.github.xiaolyuh.cls.step7.SimpleAnnotator.SIMPLE_PREFIX_STR;
import static com.github.xiaolyuh.cls.step7.SimpleAnnotator.SIMPLE_SEPARATOR_STR;

final class SimpleJsonReferenceContributor extends PsiReferenceContributor {

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(JsonStringLiteral.class),
                new PsiReferenceProvider() {
                    @Override
                    public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element,
                                                                           @NotNull ProcessingContext context) {
                        JsonStringLiteral literalExpression = (JsonStringLiteral) element;
                        String value = literalExpression.getValue();
                        if (value.startsWith(SIMPLE_PREFIX_STR + SIMPLE_SEPARATOR_STR)) {
                            TextRange property = new TextRange(SIMPLE_PREFIX_STR.length() + SIMPLE_SEPARATOR_STR.length() + 1,
                                    value.length() + 1);
                            return new PsiReference[]{new SimpleReference(element, property)};
                        }
                        return PsiReference.EMPTY_ARRAY;
                    }
                });
    }

}
