package com.github.xiaolyuh.pcel.inject;

import com.github.xiaolyuh.pcel.PointcutExpressionLanguage;
import com.google.common.collect.Sets;
import com.intellij.lang.injection.MultiHostInjector;
import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.lang.injection.general.Injection;
import com.intellij.lang.injection.general.LanguageInjectionContributor;
import com.intellij.lang.injection.general.SimpleInjection;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiNameValuePair;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class PointcutExpressionInjectionContributor implements LanguageInjectionContributor, MultiHostInjector {
    public static final Set<String> INTEREST_ANNO_SET = Sets.newHashSet(
            "org.aspectj.lang.annotation.Around",
            "org.aspectj.lang.annotation.Pointcut",
            "org.aspectj.lang.annotation.AfterThrowing"
    );

    @Override
    public @Nullable Injection getInjection(@NotNull PsiElement context) {
        if (shouldNotInject(context)) {
            return null;
        }

        return new SimpleInjection(PointcutExpressionLanguage.INSTANCE, "", "", null);
    }

    @Override
    public void getLanguagesToInject(@NotNull MultiHostRegistrar registrar,
                                     @NotNull PsiElement context) {
        if (shouldNotInject(context)) {
            return ;
        }

        PsiLiteralExpression psiLiteralExpression = (PsiLiteralExpression) context;

        registrar
                .startInjecting(PointcutExpressionLanguage.INSTANCE)
                .addPlace(null, null,
                        (PsiLanguageInjectionHost) context,
                        innerRangeStrippingQuotes(psiLiteralExpression))
                .doneInjecting();
    }

    private TextRange innerRangeStrippingQuotes(PsiLiteralExpression jsonStringLiteral) {
        TextRange textRange = jsonStringLiteral.getTextRange();
        TextRange textRange1 = textRange.shiftLeft(textRange.getStartOffset());
        return new TextRange(textRange1.getStartOffset() + 1, textRange1.getEndOffset() - 1);
    }

    private boolean shouldNotInject(PsiElement context) {
        if (!(context instanceof PsiLiteralExpression)) {
            return true;
        }

        PsiLiteralExpression psiLiteralExpression = (PsiLiteralExpression) context;
        PsiAnnotation psiAnnotation = PsiTreeUtil.getParentOfType(psiLiteralExpression, PsiAnnotation.class);
        if (psiAnnotation == null) {
            return true;
        }

        boolean contains = INTEREST_ANNO_SET.contains(psiAnnotation.getQualifiedName());
        if (!contains) {
            return true;
        }

        PsiNameValuePair psiNameValuePair = (PsiNameValuePair) psiLiteralExpression.getParent();
        return !"value".equals(psiNameValuePair.getAttributeName()) && !"pointcut".equals(psiNameValuePair.getAttributeName());
    }

    @Override
    public @NotNull List<? extends Class<? extends PsiElement>> elementsToInjectIn() {
        return List.of(PsiLiteralExpression.class);
    }

}
