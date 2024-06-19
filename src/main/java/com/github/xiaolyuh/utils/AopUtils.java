package com.github.xiaolyuh.utils;

import com.github.xiaolyuh.aop.AopMatcher;
import com.github.xiaolyuh.pcel.inject.PointcutExpressionInjectionContributor;
import com.github.xiaolyuh.pcel.psi.AopPointcut;
import com.google.common.collect.Sets;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Set;

public class AopUtils {

    /**
     * 收集方法匹配的所有切面
     * @param psiClass 方法所在类
     * @param psiMethod 方法
     * @param aspectPsiClasses 切面类集合
     * @return 匹配的切面方法集合
     */
    public static Set<PsiMethod> collectMethodMatchedAspects(PsiClass psiClass, PsiMethod psiMethod, Set<PsiClass> aspectPsiClasses) {
        Set<PsiMethod> aspectMethodMatchedSet = Sets.newHashSet();

        for (PsiClass aspectPsiClass : aspectPsiClasses) {
            Set<Pair<PsiMethod, AopPointcut>> pairSet = AopUtils.collectAspectMethods(aspectPsiClass);
            for (Pair<PsiMethod, AopPointcut> pair : pairSet) {
                AopMatcher matcher = AopMatcher.getMatcher(pair.second);
                if (matcher == null) {
                    continue;
                }

                if (matcher.methodMatcher(psiClass, psiMethod)) {
                    aspectMethodMatchedSet.add(pair.first);
                }
            }
        }

        return aspectMethodMatchedSet;
    }

    public static boolean isAspectClass(PsiClass psiClass) {
        return psiClass.getAnnotation(PointcutExpressionInjectionContributor.ASPECT_CLASS_NAME) != null;
    }

    public static Set<Pair<PsiMethod, AopPointcut>> collectAspectMethods(PsiClass aspectPsiClass) {
        Set<Pair<PsiMethod, AopPointcut>> set = Sets.newHashSet();

        PsiMethod[] methods = aspectPsiClass.getMethods();
        for (PsiMethod psiMethod : methods) {
            PsiAnnotation[] annotations = psiMethod.getAnnotations();
            for (PsiAnnotation annotation : annotations) {
                if (!PointcutExpressionInjectionContributor.INTEREST_ANNO_SET.contains(annotation.getQualifiedName())) {
                    continue;
                }

                PsiLiteralExpression psiLiteralExpression = (PsiLiteralExpression) annotation.findAttributeValue("value");
                if (psiLiteralExpression == null) {
                    continue;
                }

                List<Pair<PsiElement, TextRange>> injectedPsiFiles = InjectedLanguageManager.getInstance(aspectPsiClass.getProject())
                        .getInjectedPsiFiles(psiLiteralExpression);
                if (CollectionUtils.isEmpty(injectedPsiFiles)) {
                    continue;
                }

                Pair<PsiElement, TextRange> pair = injectedPsiFiles.get(0);
                PsiElement first = pair.getFirst();

                AopPointcut aopPointcut = PsiTreeUtil.findChildOfType(first, AopPointcut.class);
                if (aopPointcut == null) {
                    continue;
                }

                set.add(Pair.create(psiMethod, aopPointcut));
            }
        }

        return set;
    }

}
