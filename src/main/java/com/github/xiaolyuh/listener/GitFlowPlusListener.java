package com.github.xiaolyuh.listener;

import com.github.xiaolyuh.utils.ConfigUtil;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import org.jetbrains.annotations.NotNull;

public class GitFlowPlusListener implements ProjectManagerListener {

    @Override
    public void projectOpened(@NotNull Project project) {
        ConfigUtil.tryInitConfig(project);

        DumbService.getInstance(project)
                .smartInvokeLater(() -> {

                });

        ReadAction.nonBlocking(() -> "").inSmartMode(project);
    }

}
