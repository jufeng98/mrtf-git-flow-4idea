package com.github.xiaolyuh.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.openapi.wm.impl.status.widget.StatusBarEditorBasedWidgetFactory;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class GitFlowPlusStatusBarWidgetFactory extends StatusBarEditorBasedWidgetFactory {
    @Override
    public @NonNls @NotNull String getId() {
        return GitFlowPlusStatusBarWidgetFactory.class.getSimpleName();
    }

    @Override
    public @Nls @NotNull String getDisplayName() {
        return "GitFlowPlus";
    }

    @Override
    public @NotNull StatusBarWidget createWidget(@NotNull Project project) {
        return new GitFlowPlusWidget(project);
    }

    @Override
    public void disposeWidget(@NotNull StatusBarWidget widget) {

    }

}
