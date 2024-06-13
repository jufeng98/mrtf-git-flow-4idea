package com.github.xiaolyuh.aop;

import com.github.xiaolyuh.pcel.psi.PointcutExpressionAopContent;
import com.github.xiaolyuh.pcel.psi.PointcutExpressionAopKind;
import com.github.xiaolyuh.pcel.psi.PointcutExpressionAopPointcut;
import com.github.xiaolyuh.pcel.psi.PointcutExpressionAopReal;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.Nullable;

public interface AopMatcher {

    static @Nullable AopMatcher getMatcher(PointcutExpressionAopPointcut aopPointcut) {
        PointcutExpressionAopContent aopContent = aopPointcut.getAopContent();
        PointcutExpressionAopReal aopReal = aopContent.getAopReal();
        if (aopReal == null) {
            return null;
        }

        PointcutExpressionAopKind aopKind = aopReal.getAopKind();
        if (aopKind.getText().equals("@annotation")) {
            return new AnnotationAopMatcher(aopReal);
        }

        return null;
    }

    boolean methodMatcher(PsiClass psiClass, PsiMethod psiMethod);
}
