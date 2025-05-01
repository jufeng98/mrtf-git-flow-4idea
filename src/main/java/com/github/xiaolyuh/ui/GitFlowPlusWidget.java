package com.github.xiaolyuh.ui;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.CustomStatusBarWidget;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.openapi.wm.impl.status.EditorBasedWidget;
import com.intellij.openapi.wm.impl.status.TextPanel;
import com.intellij.ui.ClickListener;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.popup.PopupFactoryImpl;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * mrtf git flow 状态栏小部件
 *
 * @author yuhao.wang3
 */
public class GitFlowPlusWidget extends EditorBasedWidget implements StatusBarWidget.Multiframe, CustomStatusBarWidget {
    private final TextPanel.WithIconAndArrows iconAnArrows;
    private final Project project;

    @SuppressWarnings("deprecation")
    public GitFlowPlusWidget(@NotNull Project project) {
        super(project);
        this.project = project;

        iconAnArrows = new TextPanel.WithIconAndArrows() {
        };
        DefaultActionGroup defaultActionGroup = (DefaultActionGroup) ActionManager.getInstance().getAction("GitFlowPlus.Menu");
        iconAnArrows.setIcon(defaultActionGroup.getTemplatePresentation().getIcon());
        iconAnArrows.setBorder(WidgetBorder.WIDE);
        iconAnArrows.setText("GitFlowPlus");

        new ClickListener() {
            @Override
            public boolean onClick(@NotNull MouseEvent e, int clickCount) {
                showPopup(e);
                return true;
            }
        }.installOn(iconAnArrows);

        iconAnArrows.setVisible(true);
    }

    private void showPopup(MouseEvent e) {
        DefaultActionGroup defaultActionGroup = (DefaultActionGroup) ActionManager.getInstance().getAction("GitFlowPlus.Menu");

        JBPopupFactory popupFactory = PopupFactoryImpl.getInstance();

        ListPopup popup = popupFactory.createActionGroupPopup("",
                defaultActionGroup, DataManager.getInstance().getDataContext(iconAnArrows)
                , true, null, -1);

        Dimension dimension = popup.getContent().getPreferredSize();
        Point at = new Point(0, -dimension.height);

        Disposer.register(this, popup);

        popup.show(new RelativePoint(e.getComponent(), at));
    }

    @Override
    public JComponent getComponent() {
        return iconAnArrows;
    }

    @Override
    public @NotNull StatusBarWidget copy() {
        return new GitFlowPlusWidget(project);
    }

    @NotNull
    @Override
    public String ID() {
        return GitFlowPlusWidget.class.getName();
    }

}
