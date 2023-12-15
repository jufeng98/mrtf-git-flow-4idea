package com.github.xiaolyuh.action;

import com.github.xiaolyuh.ui.KbsMsgDialog;
import com.github.xiaolyuh.ui.ServiceDialog;
import com.github.xiaolyuh.utils.ConfigUtil;
import com.github.xiaolyuh.utils.KubesphereUtils;
import com.google.gson.JsonObject;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import org.jetbrains.annotations.NotNull;

public class ServiceRunningShowAction extends AnAction {
    @SuppressWarnings("DataFlowIssue")
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        ServiceDialog serviceDialog = new ServiceDialog("选择需要查看的服务", project);
        if (!serviceDialog.showAndGet()) {
            return;
        }
        JsonObject configObj = ConfigUtil.getProjectConfigFromFile(project);
        String runsUrl = configObj.get("runsUrl").getAsString();
        String selectService = serviceDialog.getSelectService();

        new Task.Backgroundable(project, selectService + "获取信息中...", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                Pair<String, Boolean> pair = KubesphereUtils.findInstanceName(runsUrl, selectService);
                String msg = KubesphereUtils.getContainerStartInfo(runsUrl, selectService, pair.getFirst(),
                        300, pair.getSecond());
                ApplicationManager.getApplication().invokeLater(() -> {
                    KbsMsgDialog dialog = new KbsMsgDialog("容器日志", msg, project, selectService, runsUrl,
                            pair.getFirst(), false);
                    dialog.show();
                }, ModalityState.NON_MODAL);
            }
        }.queue();
    }
}
