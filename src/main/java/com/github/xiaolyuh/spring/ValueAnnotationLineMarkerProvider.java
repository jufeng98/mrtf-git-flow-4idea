package com.github.xiaolyuh.spring;

import com.github.xiaolyuh.cls.step2.SimpleIcons;
import com.github.xiaolyuh.utils.ConfigUtil;
import com.github.xiaolyuh.utils.ValueUtils;
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.codeInsight.navigation.NavigationUtil;
import com.intellij.ide.BrowserUtil;
import com.intellij.ide.DataManager;
import com.intellij.navigation.GotoRelatedItem;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.popup.PopupFactoryImpl;
import com.intellij.util.ConstantFunction;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.List;


public final class ValueAnnotationLineMarkerProvider extends RelatedItemLineMarkerProvider {

    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element,
                                            @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        Triple<List<String>, TextRange, List<PsiElement>> triple = ValueUtils.findApolloConfig(element);
        if (triple == null) {
            return;
        }

        PsiLiteralExpression psiLiteralExpression = (PsiLiteralExpression) element;

        List<PsiElement> psiElements = triple.getRight();
        @SuppressWarnings("DialogTitleCapitalization")
        String text = psiElements.size() == 1 ? "导航到本地缓存的Apollo配置" : "找到多个本地缓存的Apollo配置,默认显示第一个";

        RelatedItemLineMarkerInfo<PsiElement> lineMarkerInfo = NavigationGutterIconBuilder
                .create(SimpleIcons.FILE)
                .setTargets(psiElements)
                .setTooltipText(text)
                .createLineMarkerInfo(psiLiteralExpression.getFirstChild());
        result.add(lineMarkerInfo);

        RelatedItemLineMarkerInfo<PsiElement> lineMarkerInfo1 = new RelatedItemLineMarkerInfo<>(
                psiLiteralExpression.getFirstChild(),
                psiLiteralExpression.getTextRange(),
                IconLoader.getIcon("/icons/apollo.svg", getClass()),
                new ConstantFunction<>("浏览器打开Apollo管理页面"),
                new ValueGutterIconNavigationHandler(triple.getLeft()),
                GutterIconRenderer.Alignment.CENTER,
                () -> GotoRelatedItem.createItems(psiElements)
        );

        result.add(lineMarkerInfo1);
    }

    public static class ValueGutterIconNavigationHandler implements GutterIconNavigationHandler<PsiElement> {
        private final List<String> appIds;

        public ValueGutterIconNavigationHandler(List<String> appIds) {
            this.appIds = appIds;
        }

        @Override
        public void navigate(MouseEvent e, PsiElement elt) {
            if (appIds.size() == 1) {
                if (openUrlFailed(appIds.get(0), elt.getProject())) {
                    showErrorTip(e);
                }
                return;
            }

            ActionGroup actionGroup = new ActionGroup("ValueGitFlowPlus", true) {
                @Override
                public AnAction @NotNull [] getChildren(@Nullable AnActionEvent e1) {
                    return appIds.stream()
                            .map(it -> new AnAction(it) {
                                @Override
                                public void actionPerformed(@NotNull AnActionEvent e2) {
                                    if (openUrlFailed(it, elt.getProject())) {
                                        showErrorTip(e);
                                    }
                                }
                            })
                            .toArray(AnAction[]::new);
                }
            };
            ListPopup popup = new PopupFactoryImpl.ActionGroupPopup("请选择AppId", actionGroup,
                    DataManager.getInstance().getDataContext(e.getComponent()),
                    false, false, true, true,
                    null, -1, null, null);

            NavigationUtil.hidePopupIfDumbModeStarts(popup, elt.getProject());

            popup.show(new RelativePoint(e));
        }

        private boolean openUrlFailed(String appId, Project project) {
            String apolloUrl = ConfigUtil.getApolloUrl(project);
            if (apolloUrl == null) {
                return true;
            }

            String path = String.format("%s/config.html#/appid=%s&env=UAT&cluster=kubesphere_test", apolloUrl, appId);
            BrowserUtil.open(path);
            return false;
        }

        @SuppressWarnings("DialogTitleCapitalization")
        private void showErrorTip(MouseEvent e) {
            JBPopupFactory.getInstance()
                    .createMessage("请先在git-flow-k8s.json中配置apolloUrl")
                    .show(new RelativePoint(e));
        }
    }

}
