package com.github.xiaolyuh.spring;

import com.github.xiaolyuh.cls.step2.SimpleIcons;
import com.github.xiaolyuh.utils.ValueUtils;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;


final class ValueAnnotationLineMarkerProvider extends RelatedItemLineMarkerProvider {

    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element,
                                            @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        Triple<String, TextRange, List<PsiElement>> triple = ValueUtils.findApolloConfig(element);
        if (triple == null) {
            return;
        }

        List<PsiElement> psiElements = triple.getRight();
        @SuppressWarnings("DialogTitleCapitalization")
        String text = psiElements.size() == 1 ? "导航到本地缓存的Apollo配置" : "找到多个本地缓存的Apollo配置,默认显示第一个";

        NavigationGutterIconBuilder<PsiElement> builder = NavigationGutterIconBuilder.create(SimpleIcons.FILE)
                .setTargets(psiElements)
                .setTooltipText(text);

        PsiLiteralExpression psiLiteralExpression = (PsiLiteralExpression) element;
        result.add(builder.createLineMarkerInfo(psiLiteralExpression.getFirstChild()));
    }

}
