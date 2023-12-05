package com.github.xiaolyuh.listener;

import com.github.xiaolyuh.ui.GitFlowPlusWidget;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vcs.VcsListener;
import com.intellij.openapi.vcs.VcsRoot;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import git4idea.GitVcs;
import org.jetbrains.annotations.NotNull;

public class GitFlowPlusListener implements ProjectManagerListener, VcsListener {
    private Project project;
    private static GitFlowPlusWidget gitFlowPlusWidget;

    @Override
    public void projectOpened(@NotNull Project project) {
        this.project = project;
        project.getMessageBus().connect().subscribe(ProjectLevelVcsManager.VCS_CONFIGURATION_CHANGED, this);
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void directoryMappingChanged() {
        VcsRoot[] vcsRoots = ProjectLevelVcsManager.getInstance(project).getAllVcsRoots();
        StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);
        //git repo present
        if (vcsRoots.length > 0 && vcsRoots[0].getVcs() instanceof GitVcs) {
            //make sure to not reinitialize the widget if it's already present
            if (gitFlowPlusWidget == null) {
                gitFlowPlusWidget = new GitFlowPlusWidget(project);

                statusBar.addWidget(gitFlowPlusWidget, "last", statusBar);
                gitFlowPlusWidget.update();
            }
        } else {
            if (gitFlowPlusWidget != null) {
                gitFlowPlusWidget.dispose();
            }
            gitFlowPlusWidget = null;
        }
    }
}
