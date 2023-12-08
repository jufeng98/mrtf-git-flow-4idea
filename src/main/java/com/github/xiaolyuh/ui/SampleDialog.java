package com.github.xiaolyuh.ui;

import com.github.xiaolyuh.utils.NotifyUtil;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;

public class SampleDialog extends DialogWrapper {

    private JPanel panel1;
    private JButton openDescBtn;
    private JButton openPopupBtn;
    private JButton openNotificationBtn;
    private JButton activeToolWindowBtn;

    public SampleDialog(@Nullable Project project) {
        super(true); // use current window as parent
        setTitle("测试DialogWrapper");
        Objects.requireNonNull(project);

        init();

        activeToolWindowBtn.addActionListener(e -> {
            close(CLOSE_EXIT_CODE);
            ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
            ToolWindow toolWindow = toolWindowManager.getToolWindow("GitflowPlus.toolWindow");
            //noinspection DataFlowIssue
            toolWindow.setAvailable(true);
            toolWindow.activate(() -> {
            });
        });

        openDescBtn.addActionListener((e) -> {
            BrowserUtil.browse("https://xiaolyuh.blog.csdn.net/article/details/105150446");
        });

        openPopupBtn.addActionListener((e) -> {
            JBPopupFactory.getInstance()
                    .createConfirmation("温馨提示", () -> {
                        JBPopupFactory.getInstance().createMessage("你选择了OK").showInFocusCenter();
                    }, 0)
                    .showInFocusCenter();
        });

        openNotificationBtn.addActionListener((e) -> {
            NotifyUtil.notifySuccess(project, "这里是成功提示");
            NotifyUtil.notifyError(project, "这里是错误提示");
            NotifyUtil.notifyInfo(project, "这里是消息提示");
        });
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return panel1;
    }
}
