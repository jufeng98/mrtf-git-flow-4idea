package com.github.xiaolyuh.spring;

import com.github.xiaolyuh.pcel.psi.AopExpr;
import com.github.xiaolyuh.pcel.psi.AopMethod;
import com.github.xiaolyuh.utils.AopUtils;
import com.google.common.collect.Lists;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
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
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class AopReferenceContributor extends PsiReferenceContributor {

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(AopExpr.class), new AopExprPsiReferenceProvider());
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(AopMethod.class), new AopMethodPsiReferenceProvider());
    }

    /**
     * 为包名和类名等创建引用,以实现点击跳转
     */
    public static class AopExprPsiReferenceProvider extends PsiReferenceProvider {
        private static final Pattern DOT_PATTERN = Pattern.compile("\\.");

        @Override
        public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element,
                                                               @NotNull ProcessingContext context) {
            if (!(element instanceof AopExpr)) {
                return PsiReference.EMPTY_ARRAY;
            }

            AopExpr aopExpr = (AopExpr) element;
            if (AopUtils.isInAtAnnotation(aopExpr)) {
                return createAtAnnotationReference(aopExpr);
            }

            return PsiReference.EMPTY_ARRAY;
        }

        private PsiReference[] createAtAnnotationReference(AopExpr aopExpr) {
            List<AopReference> references = Lists.newArrayList();
            String exprText = aopExpr.getText();
            String fullQualifierName = exprText.substring(1, exprText.length() - 1);

            Matcher matcher = DOT_PATTERN.matcher(fullQualifierName);
            List<MatchResult> matchResults = matcher.results().collect(Collectors.toList());

            Project project = aopExpr.getProject();

            int last = 0;
            // 为包名创建引用
            for (MatchResult matchResult : matchResults) {
                int start = matchResult.start();
                String packageName = fullQualifierName.substring(0, start);
                PsiPackage psiPackage = JavaPsiFacade.getInstance(project).findPackage(packageName);
                if (psiPackage == null) {
                    return references.toArray(new PsiReference[0]);
                }

                int rangeStart = last + 1;
                int rangeEnd = last + 1 + fullQualifierName.substring(last, start).length();

                TextRange textRange = new TextRange(rangeStart, rangeEnd);
                AopReference reference = new AopReference(aopExpr, textRange, psiPackage);
                references.add(reference);

                last = start + 1;
            }

            // 判断最后一截如果还是有效包名,则创建包名引用
            PsiPackage psiPackage = JavaPsiFacade.getInstance(project).findPackage(fullQualifierName);
            TextRange textRange = new TextRange(last + 1, fullQualifierName.length() + 1);
            if (psiPackage != null) {
                AopReference reference = new AopReference(aopExpr, textRange, psiPackage);
                references.add(reference);
                return references.toArray(new PsiReference[0]);
            }

            Module module = ModuleUtil.findModuleForPsiElement(aopExpr);
            if (module == null) {
                return references.toArray(new PsiReference[0]);
            }

            GlobalSearchScope scope = GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module);
            // 再判断下最后一截是否为有效类名,是则创建类名引用
            PsiClass annoPsiClass = JavaPsiFacade.getInstance(project).findClass(fullQualifierName, scope);
            if (annoPsiClass != null) {
                AopReference reference = new AopReference(aopExpr, textRange, annoPsiClass);
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
            AopMethod aopMethodReference = (AopMethod) element;
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
                AopReference reference = new AopReference(aopMethodReference, textRange, method);
                return new PsiReference[]{reference};
            }

            return PsiReference.EMPTY_ARRAY;
        }
    }

    public static final class AopReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {
        private final PsiElement targetPsiElement;

        public AopReference(@NotNull PsiElement element, TextRange textRange, PsiElement targetPsiElement) {
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
