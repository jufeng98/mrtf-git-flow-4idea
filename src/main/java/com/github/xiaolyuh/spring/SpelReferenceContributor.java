package com.github.xiaolyuh.spring;

import com.github.xiaolyuh.spel.psi.SpelFieldOrMethod;
import com.github.xiaolyuh.spel.psi.SpelFieldOrMethodName;
import com.github.xiaolyuh.spel.psi.SpelMethodCall;
import com.github.xiaolyuh.spel.psi.SpelSpel;
import com.github.xiaolyuh.utils.SpelUtils;
import com.google.common.collect.Lists;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.psi.PsiTypeElement;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class SpelReferenceContributor extends PsiReferenceContributor {

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(SpelSpel.class), new SpelPsiReferenceProvider());
    }

    public static class SpelPsiReferenceProvider extends PsiReferenceProvider {

        @Override
        public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element,
                                                               @NotNull ProcessingContext context) {
            SpelSpel spelSpel = (SpelSpel) element;
            PsiElement[] children = spelSpel.getChildren();

            PsiLanguageInjectionHost injectionHost = InjectedLanguageManager.getInstance(spelSpel.getProject()).getInjectionHost(spelSpel);
            PsiLiteralExpression psiLiteralExpression = (PsiLiteralExpression) injectionHost;
            PsiMethod psiMethod = PsiTreeUtil.getParentOfType(psiLiteralExpression, PsiMethod.class);
            if (psiMethod == null) {
                return PsiReference.EMPTY_ARRAY;
            }

            List<SpelReference> references = Lists.newArrayList();

            Object prevResolve = null;

            for (PsiElement child : children) {
                if (child instanceof SpelFieldOrMethodName) {
                    SpelFieldOrMethodName fieldOrMethodName = (SpelFieldOrMethodName) child;
                    if (SpelUtils.isSharpField(fieldOrMethodName)) {
                        if (fieldOrMethodName.getText().equals("result")) {
                            PsiTypeElement returnTypeElement = psiMethod.getReturnTypeElement();
                            TextRange textRange = fieldOrMethodName.getTextRangeInParent();
                            SpelReference reference = new SpelReference(spelSpel, textRange, returnTypeElement);
                            references.add(reference);
                            continue;
                        }

                        PsiParameter psiParameter = SpelUtils.findMethodParam(fieldOrMethodName.getText(), psiMethod);
                        if (psiParameter == null) {
                            continue;
                        }

                        prevResolve = psiParameter.getType();
                        TextRange textRange = fieldOrMethodName.getTextRangeInParent();
                        SpelReference reference = new SpelReference(spelSpel, textRange, psiParameter);
                        references.add(reference);
                    } else if (SpelUtils.isSharpMethod(fieldOrMethodName)) {
                        PsiClass psiClass = SpelUtils.getMethodContextClz(spelSpel);
                        if (psiClass == null) {
                            continue;
                        }

                        PsiMethod[] psiMethods = psiClass.findMethodsByName(fieldOrMethodName.getText(), false);
                        if (psiMethods.length == 0) {
                            continue;
                        }

                        PsiMethod method = psiMethods[0];
                        TextRange textRangeTmp = fieldOrMethodName.getTextRangeInParent();
                        SpelReference reference = new SpelReference(spelSpel, textRangeTmp, method);
                        references.add(reference);
                    }
                } else if (child instanceof SpelFieldOrMethod) {
                    SpelFieldOrMethod fieldOrMethod = (SpelFieldOrMethod) child;
                    SpelMethodCall methodCall = fieldOrMethod.getMethodCall();
                    if (methodCall == null) {
                        PsiField psiField = SpelUtils.findField(fieldOrMethod.getFieldOrMethodName().getText(), prevResolve);
                        if (psiField == null) {
                            continue;
                        }

                        prevResolve = psiField;
                        TextRange textRangeTmp = fieldOrMethod.getTextRangeInParent();
                        TextRange textRange = new TextRange(textRangeTmp.getStartOffset() + 1, textRangeTmp.getEndOffset());
                        SpelReference reference = new SpelReference(spelSpel, textRange, psiField);
                        references.add(reference);
                    }
                }

            }

            return references.toArray(new PsiReference[0]);
        }

    }

    public static final class SpelReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {
        private final PsiElement targetPsiElement;

        public SpelReference(@NotNull PsiElement element, TextRange textRange, PsiElement targetPsiElement) {
            super(element, textRange);
            this.targetPsiElement = targetPsiElement;
        }

        @Override
        public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
            PsiElementResolveResult resolveResult = new PsiElementResolveResult(targetPsiElement);
            return new ResolveResult[]{resolveResult};
        }

        @Nullable
        @Override
        public PsiElement resolve() {
            ResolveResult[] resolveResults = multiResolve(false);
            return resolveResults[0].getElement();
        }

    }

}