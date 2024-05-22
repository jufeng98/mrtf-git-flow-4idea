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


final class ValueAnnotationLineMarkerProvider extends RelatedItemLineMarkerProvider {

    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element,
                                            @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        Triple<String, TextRange, PsiElement> triple = ValueUtils.findApolloConfig(element);
        if (triple == null) {
            return;
        }

        PsiElement psiElement = triple.getRight();

        @SuppressWarnings("DialogTitleCapitalization")
        NavigationGutterIconBuilder<PsiElement> builder = NavigationGutterIconBuilder.create(SimpleIcons.FILE)
                .setTargets(psiElement)
                .setTooltipText("导航到本地缓存的Apollo配置");

        PsiLiteralExpression psiLiteralExpression = (PsiLiteralExpression) element;
        result.add(builder.createLineMarkerInfo(psiLiteralExpression.getFirstChild()));
    }

}
