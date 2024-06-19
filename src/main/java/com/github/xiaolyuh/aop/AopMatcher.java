package com.github.xiaolyuh.aop;

import com.github.xiaolyuh.pcel.psi.AopContent;
import com.github.xiaolyuh.pcel.psi.AopKind;
import com.github.xiaolyuh.pcel.psi.AopPointcut;
import com.github.xiaolyuh.pcel.psi.AopValue;
import com.github.xiaolyuh.pcel.psi.PointcutExpressionTypes;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.Nullable;

public interface AopMatcher {

    static @Nullable AopMatcher getMatcher(AopPointcut aopPointcut) {
        AopContent aopContent = aopPointcut.getContent();
        AopValue aopReal = aopContent.getValue();
        if (aopReal == null) {
            return null;
        }

        AopKind aopKind = aopReal.getKind();
        if (aopKind.getFirstChild().getNode().getElementType() == PointcutExpressionTypes.AT_ANNOTATION) {
            return new AnnotationAopMatcher(aopReal);
        }

        return null;
    }

    boolean methodMatcher(PsiClass psiClass, PsiMethod psiMethod);
}
