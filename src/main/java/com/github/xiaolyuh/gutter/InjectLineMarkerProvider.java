package com.github.xiaolyuh.gutter;

import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.codeInsight.daemon.impl.PsiElementListNavigator;
import com.intellij.ide.util.PsiClassOrFunctionalExpressionListCellRenderer;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.IconLoader;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiType;
import com.intellij.psi.search.searches.ClassInheritorsSearch;
import com.intellij.psi.util.PsiTypesUtil;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.util.Query;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("DialogTitleCapitalization")
public class InjectLineMarkerProvider implements LineMarkerProvider {

    @Override
    public LineMarkerInfo<?> getLineMarkerInfo(@NotNull PsiElement psiElement) {
        if (true) {
            return null;
        }

        if (!(psiElement instanceof PsiField)) {
            return null;
        }

        PsiField psiField = (PsiField) psiElement;
        PsiAnnotation annotation = psiField.getAnnotation("org.springframework.beans.factory.annotation.Autowired");
        if (annotation == null) {
            return null;
        }

        Icon icon = IconLoader.getIcon("/icons/config.svg", InjectLineMarkerProvider.class);

        PsiIdentifier psiIdentifier = psiField.getNameIdentifier();
        return new LineMarkerInfo<>(psiIdentifier, psiIdentifier.getTextRange(), icon, (f) -> "导航到依赖", (e, elt) -> {

            List<PsiClass> dependencies = findDependencies(psiField);
            if (dependencies.isEmpty()) {
                JBPopupFactory.getInstance().createMessage("找不到依赖").show(new RelativePoint(e));
                return;
            }

            String text = "选择" + elt.getText() + "依赖(共" + dependencies.size() + "个)";
            PsiElementListNavigator.openTargets(e, dependencies.toArray(NavigatablePsiElement.EMPTY_NAVIGATABLE_ELEMENT_ARRAY),
                    text, text, new PsiClassOrFunctionalExpressionListCellRenderer());

        }, GutterIconRenderer.Alignment.CENTER, () -> "导航到依赖");
    }

    private List<PsiClass> findDependencies(PsiField psiField) {
        PsiType psiType = psiField.getType();
        PsiClass psiClass = PsiTypesUtil.getPsiClass(psiType);
        List<PsiClass> psiClasses = Lists.newArrayList();
        if (psiClass == null) {
            return psiClasses;
        }
        if (psiClass.isInterface()) {
            // 找到所有接口的实现类
            Query<PsiClass> query = ClassInheritorsSearch.search(psiClass);
            psiClasses.addAll(query.findAll());
        } else {
            psiClasses.add(psiClass);
        }
        return psiClasses.stream()
                .filter(this::hasComponentAnno)
                .collect(Collectors.toList());
    }

    private boolean hasComponentAnno(PsiClass psiClass) {
        PsiAnnotation[] annotations = psiClass.getAnnotations();
        for (PsiAnnotation annotation : annotations) {
            String qualifiedName = annotation.getQualifiedName();
            if ("org.springframework.stereotype.Service".equals(qualifiedName)
                    || "org.springframework.stereotype.Repository".equals(qualifiedName)
                    || "org.springframework.stereotype.Component".equals(qualifiedName)) {
                return true;
            }
        }
        return false;
    }
}
