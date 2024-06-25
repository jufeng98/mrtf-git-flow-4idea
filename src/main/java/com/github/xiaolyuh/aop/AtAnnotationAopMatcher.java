package com.github.xiaolyuh.aop;

import com.github.xiaolyuh.pcel.psi.AopExpr;
import com.github.xiaolyuh.pcel.psi.AopValue;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.impl.java.stubs.index.JavaAnnotationIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class AtAnnotationAopMatcher implements AopMatcher {
    private final String fullQualifierName;

    public AtAnnotationAopMatcher(AopValue aopReal) {
        AopExpr aopExpr = aopReal.getExpr();
        @SuppressWarnings("DataFlowIssue")
        String exprText = aopExpr.getText();
        fullQualifierName = exprText.substring(1, exprText.length() - 1);
    }

    @Override
    public boolean methodMatcher(PsiClass psiClass, PsiMethod psiMethod) {
        return psiMethod.getAnnotation(fullQualifierName) != null;
    }

    @Override
    public Set<PsiMethod> collectMatchMethods(Module projectModule) {
        Project project = projectModule.getProject();
        GlobalSearchScope scope = GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(projectModule);
        PsiClass psiClass = JavaPsiFacade.getInstance(project).findClass(fullQualifierName, scope);
        if (psiClass == null) {
            return Collections.emptySet();
        }

        String className = psiClass.getName();
        if (className == null) {
            return Collections.emptySet();
        }

        Collection<PsiAnnotation> psiAnnotations = JavaAnnotationIndex.getInstance().get(className, project, scope);
        return psiAnnotations.stream()
                .map(psiAnnotation -> PsiTreeUtil.getParentOfType(psiAnnotation, PsiMethod.class))
                .collect(Collectors.toSet());
    }

}
