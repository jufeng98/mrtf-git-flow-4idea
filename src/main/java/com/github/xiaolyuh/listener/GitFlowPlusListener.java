package com.github.xiaolyuh.listener;

import com.github.xiaolyuh.utils.ConfigUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import org.jetbrains.annotations.NotNull;

public class GitFlowPlusListener implements ProjectManagerListener {

    @SuppressWarnings("removal")
    @Override
    public void projectOpened(@NotNull Project project) {
        ConfigUtil.tryInitConfig(project);
    }

}
