package com.github.xiaolyuh.aop;

import com.github.xiaolyuh.pcel.psi.AopContent;
import com.github.xiaolyuh.pcel.psi.AopPointcut;
import com.github.xiaolyuh.pcel.psi.AopValue;
import com.github.xiaolyuh.utils.AopUtils;
import com.intellij.openapi.module.Module;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface AopMatcher {

    static @Nullable AopMatcher getMatcher(AopPointcut aopPointcut) {
        AopContent aopContent = aopPointcut.getContent();
        AopValue aopValue = aopContent.getValue();

        if (AopUtils.isInAtAnnotation(aopValue)) {
            return new AtAnnotationAopMatcher(aopValue);
        }

        return null;
    }

    /**
     * 判断类的方法是否匹配切面
     */
    boolean methodMatcher(PsiClass psiClass, PsiMethod psiMethod);

    /**
     * 收集切面匹配的所有方法
     */
    Set<PsiMethod> collectMatchMethods(Module projectModule);
}
