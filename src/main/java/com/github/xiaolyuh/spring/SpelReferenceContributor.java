package com.github.xiaolyuh.spring;

import com.github.xiaolyuh.spel.psi.SpelFieldOrMethod;
import com.github.xiaolyuh.spel.psi.SpelFieldOrMethodName;
import com.github.xiaolyuh.spel.psi.SpelMethodCall;
import com.github.xiaolyuh.spel.psi.SpelMethodParam;
import com.github.xiaolyuh.spel.psi.SpelMethodParams;
import com.github.xiaolyuh.spel.psi.SpelRoot;
import com.github.xiaolyuh.spel.psi.SpelRootCombination;
import com.github.xiaolyuh.spel.psi.SpelSpel;
import com.github.xiaolyuh.spel.psi.SpelStaticT;
import com.github.xiaolyuh.utils.SpelUtils;
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
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypeElement;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.search.GlobalSearchScope;
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
            PsiLanguageInjectionHost injectionHost = InjectedLanguageManager.getInstance(spelSpel.getProject()).getInjectionHost(spelSpel);
            PsiLiteralExpression psiLiteralExpression = (PsiLiteralExpression) injectionHost;

            PsiMethod psiMethod = PsiTreeUtil.getParentOfType(psiLiteralExpression, PsiMethod.class);
            if (psiMethod == null) {
                return PsiReference.EMPTY_ARRAY;
            }

            SpelRoot spelRoot = spelSpel.getRoot();
            PsiElement[] children = spelRoot.getChildren();

            List<SpelReference> references = handleRootChildren(children, spelSpel, psiMethod);

            List<SpelRootCombination> rootCombinationList = spelSpel.getRootCombinationList();
            for (SpelRootCombination spelRootCombination : rootCombinationList) {
                SpelRoot root = spelRootCombination.getRoot();
                if (root == null) {
                    continue;
                }

                PsiElement[] list = root.getChildren();
                List<SpelReference> resList = handleRootChildren(list, spelSpel, psiMethod);
                references.addAll(resList);
            }

            return references.toArray(new PsiReference[0]);
        }

        private List<SpelReference> handleRootChildren(PsiElement[] children, SpelSpel spelSpel, PsiMethod psiMethod) {
            List<SpelReference> references = Lists.newArrayList();
            Object prevResolve = null;

            for (PsiElement child : children) {
                if (child instanceof SpelFieldOrMethodName) {
                    SpelFieldOrMethodName fieldOrMethodName = (SpelFieldOrMethodName) child;

                    prevResolve = handleSpelFieldOrMethodName(fieldOrMethodName, spelSpel, psiMethod, references);
                } else if (child instanceof SpelFieldOrMethod) {
                    SpelFieldOrMethod fieldOrMethod = (SpelFieldOrMethod) child;

                    prevResolve = handleSpelFieldOrMethod(fieldOrMethod, prevResolve, spelSpel, psiMethod, references);
                } else if (child instanceof SpelStaticT) {
                    SpelStaticT staticT = (SpelStaticT) child;

                    prevResolve = handleSpelStaticT(staticT, spelSpel, references);
                } else if (child instanceof SpelMethodCall) {
                    SpelMethodCall methodCall = (SpelMethodCall) child;

                    handleMethodCall(methodCall, spelSpel, psiMethod, references);
                }
            }

            return references;
        }

        private PsiType handleSpelFieldOrMethodName(SpelFieldOrMethodName fieldOrMethodName, SpelSpel spelSpel,
                                                    PsiMethod psiMethod, List<SpelReference> references) {
            if (SpelUtils.isSharpField(fieldOrMethodName)) {
                if (fieldOrMethodName.getText().equals("result")) {
                    PsiTypeElement returnTypeElement = psiMethod.getReturnTypeElement();
                    if (returnTypeElement == null) {
                        return null;
                    }

                    TextRange textRange = fieldOrMethodName.getTextRange();
                    SpelReference reference = new SpelReference(spelSpel, textRange, returnTypeElement);
                    references.add(reference);
                    return returnTypeElement.getType();
                }

                PsiParameter psiParameter = SpelUtils.findMethodParam(fieldOrMethodName.getText(), psiMethod);
                if (psiParameter == null) {
                    return null;
                }

                TextRange textRange = fieldOrMethodName.getTextRange();
                SpelReference reference = new SpelReference(spelSpel, textRange, psiParameter);
                references.add(reference);

                return psiParameter.getType();
            }

            if (SpelUtils.isSharpMethod(fieldOrMethodName)) {
                PsiClass contextPsiClass = SpelUtils.getMethodContextClz(spelSpel);
                if (contextPsiClass == null) {
                    return null;
                }

                PsiMethod[] psiMethods = contextPsiClass.findMethodsByName(fieldOrMethodName.getText(), false);
                if (psiMethods.length == 0) {
                    return null;
                }

                TextRange textRange = fieldOrMethodName.getTextRange();
                SpelReference reference = new SpelReference(spelSpel, textRange, psiMethods[0]);
                references.add(reference);
                return null;
            }

            return null;
        }

        private Object handleSpelFieldOrMethod(SpelFieldOrMethod fieldOrMethod, Object prevResolve,
                                               SpelSpel spelSpel, PsiMethod psiMethod, List<SpelReference> references) {
            SpelFieldOrMethodName fieldOrMethodName = fieldOrMethod.getFieldOrMethodName();
            if (fieldOrMethodName == null) {
                return null;
            }

            SpelMethodCall methodCall = fieldOrMethod.getMethodCall();
            if (methodCall == null) {
                String fieldName = fieldOrMethodName.getText();
                PsiField psiField = SpelUtils.findField(fieldName, prevResolve);
                if (psiField == null) {
                    return null;
                }

                TextRange textRangeTmp = fieldOrMethod.getTextRange();
                TextRange textRange = new TextRange(textRangeTmp.getStartOffset() + 1, textRangeTmp.getEndOffset());
                SpelReference reference = new SpelReference(spelSpel, textRange, psiField);
                references.add(reference);

                return psiField;
            }

            if (prevResolve == null) {
                return null;
            }

            String methodName = fieldOrMethodName.getText();
            PsiClass fieldPsiClass;
            if (prevResolve instanceof PsiClass) {
                fieldPsiClass = (PsiClass) prevResolve;
            } else {
                PsiClassReferenceType psiType = (PsiClassReferenceType) prevResolve;
                fieldPsiClass = psiType.resolve();
            }

            if (fieldPsiClass == null) {
                return null;
            }

            Object prevResolveInner = null;
            PsiMethod[] fieldPsiMethods = fieldPsiClass.findMethodsByName(methodName, true);
            if (fieldPsiMethods.length > 0) {
                TextRange textRange = fieldOrMethodName.getTextRange();
                // 难以判断具体是哪个方法,直接取第一个算了
                PsiMethod fieldPsiMethod = fieldPsiMethods[0];
                SpelReference reference = new SpelReference(spelSpel, textRange, fieldPsiMethod);
                references.add(reference);

                prevResolveInner = fieldPsiMethod.getReturnType();
            }


            SpelMethodParams methodParams = methodCall.getMethodParams();
            if (methodParams == null) {
                return prevResolveInner;
            }

            List<SpelMethodParam> methodParamList = methodParams.getMethodParamList();
            for (SpelMethodParam methodParam : methodParamList) {
                SpelRoot root = methodParam.getRoot();
                PsiElement[] children = root.getChildren();
                List<SpelReference> list = handleRootChildren(children, spelSpel, psiMethod);
                references.addAll(list);
            }

            return prevResolveInner;
        }

        private PsiClass handleSpelStaticT(SpelStaticT staticT, SpelSpel spelSpel, List<SpelReference> references) {
            PsiElement staticReference = staticT.getStaticReference();
            if (staticReference == null) {
                return null;
            }

            String text = staticReference.getText();
            String fullClassName = text.substring(1, text.length() - 1);

            Module module = ModuleUtil.findModuleForPsiElement(staticReference);
            if (module == null) {
                return null;
            }

            Project project = staticT.getProject();
            GlobalSearchScope scope = GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module);
            PsiClass psiClass = JavaPsiFacade.getInstance(project).findClass(fullClassName, scope);

            TextRange textRangeTmp = staticReference.getTextRange();
            TextRange textRange = new TextRange(textRangeTmp.getStartOffset() + 1, textRangeTmp.getEndOffset() - 1);
            SpelReference reference = new SpelReference(spelSpel, textRange, psiClass);
            references.add(reference);

            return psiClass;
        }

        private void handleMethodCall(SpelMethodCall methodCall, SpelSpel spelSpel, PsiMethod psiMethod, List<SpelReference> references) {
            SpelMethodParams methodParams = methodCall.getMethodParams();
            if (methodParams == null) {
                return;
            }

            for (SpelMethodParam spelMethodParam : methodParams.getMethodParamList()) {
                SpelRoot root = spelMethodParam.getRoot();
                PsiElement[] children = root.getChildren();
                List<SpelReference> list = handleRootChildren(children, spelSpel, psiMethod);
                references.addAll(list);
            }
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