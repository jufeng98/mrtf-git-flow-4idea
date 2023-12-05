package com.github.xiaolyuh.listener;

import com.github.xiaolyuh.ui.GitFlowPlusWidget;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.openapi.wm.WindowManager;
import org.jetbrains.annotations.NotNull;

public class GitFlowPlusListener implements ProjectManagerListener {

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void projectOpened(@NotNull Project project) {
        StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);
        StatusBarWidget widget = statusBar.getWidget(GitFlowPlusWidget.class.getName());
        if (widget != null) {
            return;
        }
        GitFlowPlusWidget gitFlowPlusWidget = new GitFlowPlusWidget(project);
        statusBar.addWidget(gitFlowPlusWidget, "last", statusBar);
    }

}
