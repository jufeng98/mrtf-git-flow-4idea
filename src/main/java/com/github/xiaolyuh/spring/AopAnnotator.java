package com.github.xiaolyuh.spring;

import com.github.xiaolyuh.pcel.psi.AopExpr;
import com.github.xiaolyuh.utils.AopUtils;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AopAnnotator implements Annotator {
    private static final Pattern DOT_PATTERN = Pattern.compile("\\.");

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (AopUtils.isInAtAnnotation(element)) {
            annotateAtAnnotation(element, holder);
        }
    }

    private void annotateAtAnnotation(PsiElement element, AnnotationHolder holder) {
        AopExpr aopExpr = (AopExpr) element.getParent();
        String exprText = aopExpr.getText();
        String fullQualifierName = exprText.substring(1, exprText.length() - 1);

        Matcher matcher = DOT_PATTERN.matcher(fullQualifierName);
        List<MatchResult> matchResults = matcher.results().collect(Collectors.toList());

        Project project = aopExpr.getProject();

        int last = 0;
        // 判断包名是否都存在
        for (MatchResult matchResult : matchResults) {
            int start = matchResult.start();
            String packageName = fullQualifierName.substring(0, start);
            PsiPackage psiPackage = JavaPsiFacade.getInstance(project).findPackage(packageName);
            if (psiPackage == null) {
                int rangeStart = last + 1;
                int rangeEnd = last + 1 + fullQualifierName.substring(last, start).length();
                TextRange textRange = new TextRange(rangeStart, rangeEnd).shiftRight(aopExpr.getStartOffsetInParent());
                createError("无法解析包名", holder, textRange);
                return;
            }

            last = start + 1;
        }

        PsiPackage psiPackage = JavaPsiFacade.getInstance(project).findPackage(fullQualifierName);
        TextRange textRange = new TextRange(last + 1, fullQualifierName.length() + 1).shiftRight(aopExpr.getStartOffsetInParent());
        if (psiPackage != null) {
            createError("此处应为@interface名称", holder, textRange);
            return;
        }

        Module module = ModuleUtil.findModuleForPsiElement(element);
        if (module == null) {
            return;
        }

        GlobalSearchScope scope = GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module);
        PsiClass annoPsiClass = JavaPsiFacade.getInstance(project).findClass(fullQualifierName, scope);
        if (annoPsiClass == null) {
            createError("无法解析类名", holder, textRange);
        }
    }

    private void createError(String tip, AnnotationHolder holder, TextRange textRange) {
        holder.newAnnotation(HighlightSeverity.ERROR, tip)
                .range(textRange)
                .highlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL)
                .create();
    }
}
