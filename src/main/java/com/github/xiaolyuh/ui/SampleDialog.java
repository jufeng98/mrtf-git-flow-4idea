package com.github.xiaolyuh.ui;

import com.github.xiaolyuh.utils.NotifyUtil;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class SampleDialog extends DialogWrapper {

    private JPanel panel1;
    private JButton openDescBtn;
    private JButton openPopupBtn;
    private JButton openNotificationBtn;

    public SampleDialog(@Nullable Project project) {
        super(true); // use current window as parent
        setTitle("测试DialogWrapper");

        openDescBtn.addActionListener((e) -> {
            BrowserUtil.browse("https://xiaolyuh.blog.csdn.net/article/details/105150446");
        });

        openPopupBtn.addActionListener((e) -> {
            JBPopupFactory.getInstance().createConfirmation("温馨提示", () -> {
                JBPopupFactory.getInstance().createMessage("你选择了OK").showInFocusCenter();
            }, 0).showInFocusCenter();
        });

        openNotificationBtn.addActionListener((e) -> {
            NotifyUtil.notifyInfo(project, "这里是信息");
            NotifyUtil.notifyError(project, "这里是警告");
        });

        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return panel1;
    }
}
