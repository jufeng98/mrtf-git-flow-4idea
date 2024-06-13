package com.github.xiaolyuh.aop;

import com.github.xiaolyuh.pcel.psi.PointcutExpressionAopExpr;
import com.github.xiaolyuh.pcel.psi.PointcutExpressionAopReal;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;

public class AnnotationAopMatcher implements AopMatcher {
    private final String className;

    public AnnotationAopMatcher(PointcutExpressionAopReal aopReal) {
        PointcutExpressionAopExpr aopExpr = aopReal.getAopExpr();
        String exprText = aopExpr.getText();
        className = exprText.substring(1, exprText.length() - 1);
    }

    @Override
    public boolean methodMatcher(PsiClass psiClass, PsiMethod psiMethod) {
        return psiMethod.getAnnotation(className) != null;
    }
}
