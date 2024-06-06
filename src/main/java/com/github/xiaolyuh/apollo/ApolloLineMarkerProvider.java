package com.github.xiaolyuh.apollo;

import com.github.xiaolyuh.spring.ValueAnnotationLineMarkerProvider;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.lang.properties.IProperty;
import com.intellij.navigation.GotoRelatedItem;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.util.IconLoader;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.ConstantFunction;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

public class ApolloLineMarkerProvider extends RelatedItemLineMarkerProvider {

    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element,
                                            @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        PsiFile containingFile = element.getContainingFile();
        if (containingFile == null) {
            return;
        }

        String name = containingFile.getName();
        if (!name.equals("app.properties")) {
            return;
        }

        if (!(element instanceof IProperty)) {
            return;
        }

        IProperty property = (IProperty) element;
        if (!property.getName().equals("app.id")) {
            return;
        }

        String appId = property.getValue();

        RelatedItemLineMarkerInfo<PsiElement> lineMarkerInfo = new RelatedItemLineMarkerInfo<>(
                element,
                element.getTextRange(),
                IconLoader.getIcon("/icons/apollo.svg", getClass()),
                new ConstantFunction<>("浏览器打开Apollo管理页面"),
                new ValueAnnotationLineMarkerProvider.ValueGutterIconNavigationHandler(Collections.singletonList(appId)),
                GutterIconRenderer.Alignment.CENTER,
                () -> GotoRelatedItem.createItems(Collections.emptyList())
        );

        result.add(lineMarkerInfo);
    }

}
