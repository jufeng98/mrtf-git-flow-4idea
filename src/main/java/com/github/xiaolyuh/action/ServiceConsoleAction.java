package com.github.xiaolyuh.action;

import com.github.xiaolyuh.ui.JcefK8sConsoleDialog;
import com.github.xiaolyuh.ui.ServiceDialog;
import com.github.xiaolyuh.utils.ConfigUtil;
import com.github.xiaolyuh.utils.ExecutorUtils;
import com.github.xiaolyuh.utils.KubesphereUtils;
import com.github.xiaolyuh.utils.NotifyUtil;
import com.github.xiaolyuh.utils.StringUtils;
import com.github.xiaolyuh.vo.InstanceVo;
import com.google.gson.JsonObject;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;

public class ServiceConsoleAction extends AnAction implements DumbAware {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        ServiceDialog serviceDialog = new ServiceDialog("选择需要连接的服务控制台", project);
        serviceDialog.selectLastChoose();
        if (!serviceDialog.showAndGet()) {
            return;
        }
        JsonObject configObj = ConfigUtil.getProjectConfigFromFile(project);
        if (configObj == null) {
            NotifyUtil.notifyError(project, "请先更新配置");
            return;
        }
        String runsUrl = configObj.get("runsUrl").getAsString();
        String selectService = serviceDialog.getSelectService();
        if (StringUtils.isBlank(selectService)) {
            return;
        }

        new Task.Backgroundable(project, selectService + "获取信息中...", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                List<InstanceVo> instanceVos;
                try {
                    instanceVos = KubesphereUtils.findInstanceName(runsUrl, selectService);
                } catch (Exception e) {
                    NotifyUtil.notifyError(project, ExceptionUtils.getStackTrace(e));
                    return;
                }
                if (instanceVos.isEmpty()) {
                    NotifyUtil.notifyError(project, "没有任何服务实例!");
                    return;
                }
                ExecutorUtils.addTask(() -> SwingUtilities
                        .invokeLater(() -> showInstances(project, instanceVos, runsUrl, selectService)));
            }
        }.queue();
    }

    @SuppressWarnings("DialogTitleCapitalization")
    public void showInstances(Project project, List<InstanceVo> instanceVos, String runsUrl, String selectService) {
        final int[] choose = {0};
        if (instanceVos.size() > 1) {
            String[] options = instanceVos.stream()
                    .map(instanceVo -> instanceVo.getDesc() + ":" + instanceVo.getName())
                    .toArray(String[]::new);
            choose[0] = Messages.showDialog(project, "找到多个服务实例,请选择:", "温馨提示",
                    options, 0, null);
        }
        if (choose[0] == -1) {
            return;
        }
        InstanceVo instanceVo = instanceVos.get(choose[0]);

        ExecutorUtils.addTask(() -> SwingUtilities.invokeLater(() ->
                showInstanceDialog(project, instanceVo, runsUrl, selectService)));

    }

    public void showInstanceDialog(Project project, InstanceVo instanceVo, String runsUrl, String selectService) {
        ApplicationManager.getApplication().invokeLater(() -> {
            try {
                new JcefK8sConsoleDialog(instanceVo, runsUrl, project, selectService);
            } catch (Exception e) {
                NotifyUtil.notifyError(project, e.getMessage());
            }
        }, ModalityState.NON_MODAL);
    }
}
