package com.github.xiaolyuh.action;

import com.github.xiaolyuh.ui.KbsMsgDialog;
import com.github.xiaolyuh.ui.ServiceDialog;
import com.github.xiaolyuh.utils.ConfigUtil;
import com.github.xiaolyuh.utils.ExecutorUtils;
import com.github.xiaolyuh.utils.KubesphereUtils;
import com.github.xiaolyuh.utils.NotifyUtil;
import com.github.xiaolyuh.utils.StringUtils;
import com.github.xiaolyuh.vo.InstanceVo;
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

public class ServiceLogAction extends AnAction implements DumbAware {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        ServiceDialog serviceDialog = new ServiceDialog("选择需要查看的服务", project);
        serviceDialog.selectLastChoose();
        if (!serviceDialog.showAndGet()) {
            return;
        }
        if (ConfigUtil.notExistsK8sOptions(project)) {
            NotifyUtil.notifyError(project, "缺少k8s配置");
            return;
        }
        String selectService = serviceDialog.getSelectService();
        if (StringUtils.isBlank(selectService)) {
            return;
        }

        new Task.Backgroundable(project, selectService + "获取信息中...", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                List<InstanceVo> instanceVos;
                try {
                    instanceVos = KubesphereUtils.findInstanceName(project, selectService);
                } catch (Exception e) {
                    NotifyUtil.notifyError(project, ExceptionUtils.getStackTrace(e));
                    return;
                }
                if (instanceVos.isEmpty()) {
                    NotifyUtil.notifyError(project, "没有任何服务实例!");
                    return;
                }
                ExecutorUtils.addTask(() -> SwingUtilities
                        .invokeLater(() -> showInstances(project, instanceVos, selectService)));
            }
        }.queue();
    }

    @SuppressWarnings("DialogTitleCapitalization")
    public void showInstances(Project project, List<InstanceVo> instanceVos, String selectService) {
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

        new Task.Backgroundable(project, instanceVo.getName() + "获取信息中...", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                byte[] textBytes;
                try {
                    textBytes = KubesphereUtils.getContainerStartInfo(project, selectService, instanceVo.getName(),
                            500, instanceVo.isPreviews(), false);
                } catch (Exception e) {
                    NotifyUtil.notifyError(project, ExceptionUtils.getStackTrace(e));
                    return;
                }
                ExecutorUtils.addTask(() -> SwingUtilities.invokeLater(() -> showInstanceDialog(project, instanceVo,
                        textBytes, selectService)));
            }
        }.queue();
    }

    public void showInstanceDialog(Project project, InstanceVo instanceVo, byte[] textBytes, String selectService) {
        ApplicationManager.getApplication().invokeLater(() -> {
            String title = instanceVo.getDesc() + ":" + instanceVo.getName();
            KbsMsgDialog dialog = new KbsMsgDialog(title, textBytes, project, selectService, instanceVo.getName(), false);
            dialog.show();
        }, ModalityState.NON_MODAL);
    }
}
