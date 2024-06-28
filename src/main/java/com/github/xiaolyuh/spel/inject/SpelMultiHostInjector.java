package com.github.xiaolyuh.spel.inject;

import com.github.xiaolyuh.spel.SpelLanguage;
import com.google.common.collect.Sets;
import com.intellij.lang.injection.MultiHostInjector;
import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiNameValuePair;
import com.intellij.psi.PsiPolyadicExpression;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public class SpelMultiHostInjector implements MultiHostInjector {
    public static final Set<String> INTEREST_ANNO_SET = Sets.newHashSet(
            "cn.com.bluemoon.common.annos.AopLock",
            "cn.com.bluemoon.common.annos.AopLogRecord",
            "cn.com.bluemoon.washservice.common.annos.AopLock"
    );
    public static final Set<String> INTEREST_ATTR_NAME = Sets.newHashSet("lockKeySpEL", "type", "desc");

    @Override
    public void getLanguagesToInject(@NotNull MultiHostRegistrar registrar, @NotNull PsiElement context) {
        if (!shouldInject(context)) {
            return;
        }

        if (context instanceof PsiPolyadicExpression) {
            PsiPolyadicExpression psiPolyadicExpression = (PsiPolyadicExpression) context;
            PsiExpression[] operands = psiPolyadicExpression.getOperands();

            registrar.startInjecting(SpelLanguage.INSTANCE);
            for (PsiExpression operand : operands) {
                TextRange textRange = innerRangeStrippingQuotes(operand);
                registrar.addPlace(null, null, (PsiLanguageInjectionHost) operand, textRange);
            }
            registrar.doneInjecting();
        } else {
            PsiElement parent = context.getParent();
            if (parent instanceof PsiPolyadicExpression) {
                return;
            }

            TextRange textRange = innerRangeStrippingQuotes(context);
            registrar
                    .startInjecting(SpelLanguage.INSTANCE)
                    .addPlace(null, null, (PsiLanguageInjectionHost) context, textRange)
                    .doneInjecting();
        }
    }

    @Override
    public @NotNull List<? extends Class<? extends PsiElement>> elementsToInjectIn() {
        return List.of(PsiLiteralExpression.class, PsiPolyadicExpression.class);
    }

    private boolean shouldInject(PsiElement context) {
        if (!(context instanceof PsiLiteralExpression) && !(context instanceof PsiPolyadicExpression)) {
            return false;
        }

        PsiElement parent = context.getParent();
        if (parent == null) {
            return false;
        }

        PsiNameValuePair psiNameValuePair = PsiTreeUtil.getParentOfType(context, PsiNameValuePair.class);
        if (psiNameValuePair == null) {
            return false;
        }

        String attributeName = psiNameValuePair.getAttributeName();
        if (!INTEREST_ATTR_NAME.contains(attributeName)) {
            return false;
        }

        PsiAnnotation psiAnnotation = PsiTreeUtil.getParentOfType(context, PsiAnnotation.class);
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


    private TextRange innerRangeStrippingQuotes(PsiElement context) {
        TextRange textRange = context.getTextRange();
        TextRange textRangeTmp = textRange.shiftLeft(textRange.getStartOffset());
        return new TextRange(textRangeTmp.getStartOffset() + 1, textRangeTmp.getEndOffset() - 1);
    }
}
