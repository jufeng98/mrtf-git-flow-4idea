package com.github.xiaolyuh.spring;

import com.github.xiaolyuh.pcel.psi.PointcutExpressionAopExpr;
import com.github.xiaolyuh.pcel.psi.PointcutExpressionAopKind;
import com.github.xiaolyuh.pcel.psi.PointcutExpressionAopMethodReference;
import com.github.xiaolyuh.pcel.psi.PointcutExpressionAopReal;
import com.google.common.collect.Lists;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiUtil;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AopReferenceContributor extends PsiReferenceContributor {

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(PointcutExpressionAopExpr.class), new AopPsiReferenceProvider());
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(PointcutExpressionAopMethodReference.class), new AopMethodPsiReferenceProvider());
    }

    /**
     * 为包名和类名等创建引用,以实现点击跳转
     */
    public static class AopPsiReferenceProvider extends PsiReferenceProvider {
        private final Pattern pattern = Pattern.compile("\\.");

        @Override
        public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element,
                                                               @NotNull ProcessingContext context) {
            PointcutExpressionAopExpr aopExpr = (PointcutExpressionAopExpr) element;
            PointcutExpressionAopReal aopReal = (PointcutExpressionAopReal) aopExpr.getParent();

            PointcutExpressionAopKind aopKind = aopReal.getAopKind();
            if (aopKind.getText().equals("@annotation")) {
                return handleAnnoReference(aopExpr);
            }

            return PsiReference.EMPTY_ARRAY;
        }

        private PsiReference[] handleAnnoReference(PointcutExpressionAopExpr aopExpr) {
            String exprText = aopExpr.getText();
            exprText = exprText.substring(1, exprText.length() - 1);

            List<AopAnnotationClsReference> references = Lists.newArrayList();

            Matcher matcher = pattern.matcher(exprText);
            int last = 0;
            while ((matcher.find())) {
                int start = matcher.start();

                PsiPackage psiPackage = JavaPsiFacade.getInstance(aopExpr.getProject()).findPackage(exprText.substring(0, start));
                if (psiPackage == null) {
                    continue;
                }
                TextRange textRange = new TextRange(last + 1, last + 1 + exprText.substring(last, start).length());

                AopAnnotationClsReference reference = new AopAnnotationClsReference(aopExpr, textRange, psiPackage);
                references.add(reference);

                last = start + 1;
            }

            PsiClass annoPsiClass = JavaPsiFacade.getInstance(aopExpr.getProject()).findClass(exprText,
                    GlobalSearchScope.everythingScope(aopExpr.getProject()));
            if (annoPsiClass != null) {
                TextRange textRange = new TextRange(last + 1, exprText.length() + 1);
                AopAnnotationClsReference reference = new AopAnnotationClsReference(aopExpr, textRange, annoPsiClass);
                references.add(reference);
            }

            return references.toArray(new PsiReference[0]);
        }
    }

    /**
     * 为方法名创建引用,以实现点击跳转
     */
    public static class AopMethodPsiReferenceProvider extends PsiReferenceProvider {

        @Override
        public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
            PointcutExpressionAopMethodReference aopMethodReference = (PointcutExpressionAopMethodReference) element;
            String text = aopMethodReference.getText();

            int idx = text.indexOf("(");
            if (idx == -1) {
                return PsiReference.EMPTY_ARRAY;
            }

            PsiLanguageInjectionHost injectionHost = InjectedLanguageManager.getInstance(aopMethodReference.getProject()).getInjectionHost(aopMethodReference);
            if (injectionHost == null) {
                return PsiReference.EMPTY_ARRAY;
            }

            PsiLiteralExpression psiLiteralExpression = (PsiLiteralExpression) injectionHost;
            PsiClass psiClass = PsiUtil.getTopLevelClass(psiLiteralExpression);
            if (psiClass == null) {
                return PsiReference.EMPTY_ARRAY;
            }

            String methodName = text.substring(0, idx);
            for (PsiMethod method : psiClass.getMethods()) {
                if (!method.getName().equals(methodName)) {
                    continue;
                }

                TextRange textRange = new TextRange(0, idx);
                AopAnnotationClsReference reference = new AopAnnotationClsReference(aopMethodReference, textRange, method);
                return new PsiReference[]{reference};
            }

            return PsiReference.EMPTY_ARRAY;
        }
    }

    public static final class AopAnnotationClsReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {
        private final PsiElement targetPsiElement;

        public AopAnnotationClsReference(@NotNull PsiElement element, TextRange textRange, PsiElement targetPsiElement) {
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
