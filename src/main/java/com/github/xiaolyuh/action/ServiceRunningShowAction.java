package com.github.xiaolyuh.action;

import com.github.xiaolyuh.ui.KbsMsgDialog;
import com.github.xiaolyuh.ui.ServiceDialog;
import com.github.xiaolyuh.utils.ConfigUtil;
import com.github.xiaolyuh.utils.KubesphereUtils;
import com.github.xiaolyuh.utils.NotifyUtil;
import com.github.xiaolyuh.vo.InstanceVo;
import com.google.gson.JsonObject;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;

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
                List<InstanceVo> instanceVos = KubesphereUtils.findInstanceName(runsUrl, selectService);
                if (instanceVos.isEmpty()) {
                    NotifyUtil.notifyError(project, "没有任何实例!");
                    return;
                }
                final int[] choose = {0};
                if (instanceVos.size() > 1) {
                    try {
                        SwingUtilities.invokeAndWait(() -> {
                            String[] options = instanceVos.stream()
                                    .map(instanceVo -> instanceVo.getName() + instanceVo.getDesc()).toArray(String[]::new);
                            choose[0] = Messages.showDialog(project, "找到多个服务实例,请选择:", "温馨提示",
                                    options, 0, null);
                        });
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
                if (choose[0] == -1) {
                    return;
                }
                InstanceVo instanceVo = instanceVos.get(choose[0]);
                byte[] textBytes = KubesphereUtils.getContainerStartInfo(runsUrl, selectService, instanceVo.getName(),
                        300, instanceVo.isPreviews(), false);
                ApplicationManager.getApplication().invokeLater(() -> {
                    KbsMsgDialog dialog = new KbsMsgDialog(selectService, textBytes, project, selectService, runsUrl,
                            instanceVo.getName(), false);
                    dialog.show();
                }, ModalityState.NON_MODAL);
            }
        }.queue();
    }
}
