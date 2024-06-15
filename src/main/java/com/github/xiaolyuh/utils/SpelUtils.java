package com.github.xiaolyuh.utils;

import com.github.xiaolyuh.spel.psi.SpelFieldOrMethodName;
import com.github.xiaolyuh.spel.psi.SpelMethodCall;
import com.github.xiaolyuh.spel.psi.SpelSpel;
import com.github.xiaolyuh.spel.psi.SpelTypes;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiParameterList;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.Nullable;

public class SpelUtils {

    public static PsiClass getMethodContextClz(SpelSpel spelSpel) {
        return JavaPsiFacade.getInstance(spelSpel.getProject()).findClass(
                "cn.com.bluemoon.washing.aspect.AopLogRecordAspect.AopLogRecordUtils",
                GlobalSearchScope.projectScope(spelSpel.getProject())
        );
    }

    public static boolean isSharpField(SpelFieldOrMethodName fieldOrMethodName) {
        PsiElement prevSibling = fieldOrMethodName.getPrevSibling();
        if (prevSibling.getNode().getElementType() != SpelTypes.SHARP) {
            return false;
        }

        PsiElement nextSibling = fieldOrMethodName.getNextSibling();
        if (nextSibling == null) {
            return true;
        }

        PsiElement firstChild = nextSibling.getFirstChild();
        if (firstChild == null) {
            return false;
        }

        return firstChild.getNode().getElementType() == SpelTypes.DOT
                || firstChild.getNode().getElementType() == SpelTypes.PROJECTION
                || firstChild.getNode().getElementType() == SpelTypes.SELECTION
                || firstChild.getNode().getElementType() == SpelTypes.L_BRACKET;
    }

    public static boolean isSharpMethod(SpelFieldOrMethodName fieldOrMethodName) {
        PsiElement prevSibling = fieldOrMethodName.getPrevSibling();
        if (prevSibling.getNode().getElementType() != SpelTypes.SHARP) {
            return false;
        }

        PsiElement nextSibling = fieldOrMethodName.getNextSibling();
        if (nextSibling == null) {
            return false;
        }

        return nextSibling instanceof SpelMethodCall;
    }

    @Nullable
    public static PsiParameter findMethodParam(String fieldName, PsiMethod psiMethod) {
        PsiParameterList parameterList = psiMethod.getParameterList();
        PsiParameter[] parameters = parameterList.getParameters();
        for (PsiParameter parameter : parameters) {
            if (!parameter.getName().equals(fieldName)) {
                continue;
            }

            return parameter;
        }

        return null;
    }

    @Nullable
    public static PsiField findField(String fieldName, Object prevResolve) {
        if (prevResolve == null) {
            return null;
        }

        if (prevResolve instanceof PsiClassType) {
            PsiClassType psiClassType = (PsiClassType) prevResolve;
            PsiClass psiClass = psiClassType.resolve();
            if (psiClass == null) {
                return null;
            }

            return psiClass.findFieldByName(fieldName, true);
        }

        if (prevResolve instanceof PsiField) {
            PsiField psiField = (PsiField) prevResolve;
            return findField(fieldName, psiField.getType());
        }

        return null;
    }

}
