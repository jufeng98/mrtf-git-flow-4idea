package com.github.xiaolyuh.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.jcef.JBCefBrowser;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class JcefDialog extends DialogWrapper {
    private JPanel panel;

    protected JcefDialog(@Nullable Project project) {
        super(project);
        init();
        JBCefBrowser browser = new JBCefBrowser("https://www.baidu.com");
        panel.add(browser.getComponent(), BorderLayout.CENTER);
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return panel;
    }
}
