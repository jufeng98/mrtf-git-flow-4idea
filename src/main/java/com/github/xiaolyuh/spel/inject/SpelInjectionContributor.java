package com.github.xiaolyuh.spel.inject;

import com.github.xiaolyuh.spel.SpelLanguage;
import com.google.common.collect.Sets;
import com.intellij.lang.injection.general.Injection;
import com.intellij.lang.injection.general.LanguageInjectionContributor;
import com.intellij.lang.injection.general.SimpleInjection;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiNameValuePair;
import com.intellij.psi.PsiPolyadicExpression;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class SpelInjectionContributor implements LanguageInjectionContributor {
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
        if (shouldInject(context)) {
            return new SimpleInjection(SpelLanguage.INSTANCE, "", "", null);
        }

        return null;
    }

    private boolean shouldInject(PsiElement context) {
        if (context instanceof PsiPolyadicExpression) {
//            PsiPolyadicExpression psiPolyadicExpression = (PsiPolyadicExpression) context;
//            return true;
        }

        if (!(context instanceof PsiLiteralExpression)) {
            return false;
        }

        PsiLiteralExpression psiLiteralExpression = (PsiLiteralExpression) context;
        PsiElement parent = psiLiteralExpression.getParent();
        if (parent == null) {
            return false;
        }

        if (!(parent instanceof PsiNameValuePair) && !(parent.getParent() instanceof PsiNameValuePair)) {
            return false;
        }

        PsiNameValuePair psiNameValuePair = (PsiNameValuePair) (parent instanceof PsiNameValuePair ? parent : parent.getParent());
        String attributeName = psiNameValuePair.getAttributeName();
        if (!INTEREST_ATTR_NAME.contains(attributeName)) {
            return false;
        }

        PsiAnnotation psiAnnotation = PsiTreeUtil.getParentOfType(psiLiteralExpression, PsiAnnotation.class);
        if (psiAnnotation == null) {
            return false;
        }

        boolean contains = INTEREST_ANNO_SET.contains(psiAnnotation.getQualifiedName());
        if (!contains) {
            return false;
        }

        PsiClass psiClass = PsiUtil.getTopLevelClass(psiAnnotation);
        return psiClass != null;
    }

}
