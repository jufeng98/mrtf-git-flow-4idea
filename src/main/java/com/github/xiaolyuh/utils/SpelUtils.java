package com.github.xiaolyuh.utils;

import com.github.xiaolyuh.spel.psi.SpelFieldOrMethodName;
import com.github.xiaolyuh.spel.psi.SpelMethodCall;
import com.github.xiaolyuh.spel.psi.SpelSpel;
import com.github.xiaolyuh.spel.psi.SpelTypes;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiParameterList;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.Nullable;

public class SpelUtils {
    private static final String AOP_LOG_RECORD_UTILS = "cn.com.bluemoon.washing.aspect.AopLogRecordAspect.AopLogRecordUtils";

    public static PsiClass getMethodContextClz(SpelSpel spelSpel) {
        Module module = ModuleUtil.findModuleForPsiElement(spelSpel);
        if (module == null) {
            return null;
        }

        Project project = spelSpel.getProject();
        GlobalSearchScope scope = GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module);

        return JavaPsiFacade.getInstance(project).findClass(AOP_LOG_RECORD_UTILS, scope);
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

    /**
     * 找到字段名对应的方法参数
     *
     * @return 方法参数
     */
    @Nullable
    public static PsiParameter findMethodParamRelateToField(String fieldName, PsiMethod psiMethod) {
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
    public static PsiClass resolvePsiType(PsiType psiType) {
        PsiClassReferenceType referenceType = (PsiClassReferenceType) psiType;
        if (referenceType != null) {
            return referenceType.resolve();
        }

        return null;
    }
}
