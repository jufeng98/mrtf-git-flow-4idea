package com.github.xiaolyuh.spel.inject;

import com.github.xiaolyuh.spel.SpelLanguage;
import com.google.common.collect.Sets;
import com.intellij.lang.injection.MultiHostInjector;
import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.lang.injection.general.Injection;
import com.intellij.lang.injection.general.LanguageInjectionContributor;
import com.intellij.lang.injection.general.SimpleInjection;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiNameValuePair;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class SpelInjectionContributor implements LanguageInjectionContributor, MultiHostInjector {
    public static final Set<String> INTEREST_ANNO_SET = Sets.newHashSet(
            "cn.com.bluemoon.common.annos.AopLock",
            "cn.com.bluemoon.common.annos.AopLogRecord",
            "cn.com.bluemoon.washservice.common.annos.AopLock"
    );
    public static final Set<String> INTEREST_ATTR_NAME = Sets.newHashSet(
            "lockKeySpEL",
            "type",
            "desc"
    );

    @Override
    public @Nullable Injection getInjection(@NotNull PsiElement context) {
        if (shouldNotInject(context)) {
            return null;
        }

        return new SimpleInjection(SpelLanguage.INSTANCE, "", "", null);
    }

    @Override
    public void getLanguagesToInject(@NotNull MultiHostRegistrar registrar,
                                     @NotNull PsiElement context) {
        if (shouldNotInject(context)) {
            return;
        }

        PsiLiteralExpression psiLiteralExpression = (PsiLiteralExpression) context;

        registrar
                .startInjecting(SpelLanguage.INSTANCE)
                .addPlace(null, null,
                        (PsiLanguageInjectionHost) context,
                        innerRangeStrippingQuotes(psiLiteralExpression))
                .doneInjecting();
    }

    private TextRange innerRangeStrippingQuotes(PsiElement jsonStringLiteral) {
        TextRange textRange = jsonStringLiteral.getTextRange();
        TextRange textRange1 = textRange.shiftLeft(textRange.getStartOffset());
        return new TextRange(textRange1.getStartOffset() + 1, textRange1.getEndOffset() - 1);
    }

    private boolean shouldNotInject(PsiElement context) {
        if (!(context instanceof PsiLiteralExpression)) {
            return true;
        }

        PsiLiteralExpression psiLiteralExpression = (PsiLiteralExpression) context;
        PsiElement parent = psiLiteralExpression.getParent();
        if (parent == null) {
            return true;
        }

        if (!(parent instanceof PsiNameValuePair) && !(parent.getParent() instanceof PsiNameValuePair)) {
            return true;
        }

        PsiNameValuePair psiNameValuePair = (PsiNameValuePair) (parent instanceof PsiNameValuePair ? parent : parent.getParent());
        String attributeName = psiNameValuePair.getAttributeName();
        if (!INTEREST_ATTR_NAME.contains(attributeName)) {
            return true;
        }

        PsiAnnotation psiAnnotation = PsiTreeUtil.getParentOfType(psiLiteralExpression, PsiAnnotation.class);
        if (psiAnnotation == null) {
            return true;
        }

        boolean contains = INTEREST_ANNO_SET.contains(psiAnnotation.getQualifiedName());
        if (!contains) {
            return true;
        }

        PsiClass psiClass = PsiUtil.getTopLevelClass(psiAnnotation);
        return psiClass == null;
    }

    @Override
    public @NotNull List<? extends Class<? extends PsiElement>> elementsToInjectIn() {
        return List.of(PsiLiteralExpression.class);
    }

}
