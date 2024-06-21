package com.github.xiaolyuh.spring;

import com.github.xiaolyuh.pcel.psi.PointcutExpressionTypes;
import com.github.xiaolyuh.utils.AopUtils;
import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.util.ParenthesesInsertHandler;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.impl.JavaPsiFacadeImpl;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;

public class AopCompletionContributor extends CompletionContributor {
    private static final String[] DESIGNATORS = {"@annotation", "@target", "execution", "target", "bean"};

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        PsiElement position = parameters.getPosition();

        if (position.getNode().getElementType() == PointcutExpressionTypes.WORD) {
            fillDesignators(result);
            return;
        }

        if (AopUtils.isInAtAnnotation(position)) {
            fillPackageAndAnnoClass(position, result);
        }
    }

    /**
     * 填入AOP指示符
     */
    private void fillDesignators(CompletionResultSet result) {
        for (String suggestion : DESIGNATORS) {
            LookupElementBuilder bold = LookupElementBuilder.create(suggestion)
                    .withInsertHandler(ParenthesesInsertHandler.WITH_PARAMETERS)
                    .bold();
            result.addElement(bold);
        }
    }

    /**
     * 填入当前包名下的所有子包名以及包名下的注解类名
     */
    private void fillPackageAndAnnoClass(PsiElement position, CompletionResultSet result) {
        Module module = ModuleUtil.findModuleForPsiElement(position);
        if (module == null) {
            return;
        }

        Project project = position.getProject();
        JavaPsiFacadeImpl javaPsiFacade = (JavaPsiFacadeImpl) JavaPsiFacade.getInstance(project);
        GlobalSearchScope scope = GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module);

        String prefix = result.getPrefixMatcher().getPrefix();
        int idx = prefix.lastIndexOf(".");
        if (idx == -1) {
            // 处理第一层包名提示
            PsiPackage firstLevelPackage = javaPsiFacade.findPackage("");
            if (firstLevelPackage == null) {
                return;
            }
            PsiPackage[] subPackages = javaPsiFacade.getSubPackages(firstLevelPackage, scope);
            fillSubPackage(subPackages, result);
            return;
        }

        // 截取得到包名
        String packageName = prefix.substring(0, idx);
        PsiPackage psiPackage = javaPsiFacade.findPackage(packageName);
        if (psiPackage == null) {
            return;
        }

        String newPrefix = prefix.substring(idx + 1);
        // 按照新的前缀进行匹配提示
        CompletionResultSet completionResultSet = result.withPrefixMatcher(newPrefix);

        PsiClass[] classes = psiPackage.getClasses();
        fillAnnotationClass(classes, completionResultSet);

        PsiPackage[] subPackages = psiPackage.getSubPackages();
        fillSubPackage(subPackages, completionResultSet);
    }

    private void fillAnnotationClass(PsiClass[] classes, CompletionResultSet result) {
        for (PsiClass psiClass : classes) {
            if (!psiClass.isAnnotationType()) {
                continue;
            }
            result.addElement(LookupElementBuilder.create(psiClass));
        }
    }

    private void fillSubPackage(PsiPackage[] subPackages, CompletionResultSet result) {
        for (PsiPackage subPackage : subPackages) {
            result.addElement(LookupElementBuilder.create(subPackage));
        }
    }
}
