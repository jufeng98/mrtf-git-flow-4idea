package com.github.xiaolyuh.aop;

import com.github.xiaolyuh.pcel.psi.AopContent;
import com.github.xiaolyuh.pcel.psi.AopPointcut;
import com.github.xiaolyuh.pcel.psi.AopValue;
import com.github.xiaolyuh.utils.AopUtils;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.Nullable;

public interface AopMatcher {

    static @Nullable AopMatcher getMatcher(AopPointcut aopPointcut) {
        AopContent aopContent = aopPointcut.getContent();
        AopValue aopValue = aopContent.getValue();

        if (AopUtils.isInAtAnnotation(aopValue)) {
            return new AtAnnotationAopMatcher(aopValue);
        }

        return null;
    }

    boolean methodMatcher(PsiClass psiClass, PsiMethod psiMethod);
}
