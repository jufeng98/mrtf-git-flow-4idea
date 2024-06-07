package com.github.xiaolyuh.spring;

import com.github.xiaolyuh.pcel.psi.PointcutExpressionAopExpr;
import com.github.xiaolyuh.pcel.psi.PointcutExpressionAopKind;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class AopReferenceContributor extends PsiReferenceContributor {

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(StandardPatterns.instanceOf(PointcutExpressionAopExpr.class), new AopPsiReferenceProvider());
    }

    public static class AopPsiReferenceProvider extends PsiReferenceProvider {
        @Override
        public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element,
                                                               @NotNull ProcessingContext context) {
            PointcutExpressionAopExpr aopExpr = (PointcutExpressionAopExpr) element;
            PointcutExpressionAopKind aopKind = (PointcutExpressionAopKind) aopExpr.getPrevSibling();

            if (aopKind.getText().equals("@annotation")) {
                return handleAnnoReference(aopExpr);
            }

            return PsiReference.EMPTY_ARRAY;
        }

        private PsiReference[] handleAnnoReference(PointcutExpressionAopExpr aopExpr) {
            String exprText = aopExpr.getText();
            exprText = exprText.substring(1, exprText.length() - 2);

            PsiClass annoPsiClass = JavaPsiFacade.getInstance(aopExpr.getProject())
                    .findClass(exprText, GlobalSearchScope.everythingScope(aopExpr.getProject()));
            if (annoPsiClass == null) {
                return PsiReference.EMPTY_ARRAY;
            }

            TextRange textRange = new TextRange(1, exprText.length() - 2);
            return new PsiReference[]{new AopAnnotationClsReference(aopExpr, textRange, annoPsiClass)};
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
