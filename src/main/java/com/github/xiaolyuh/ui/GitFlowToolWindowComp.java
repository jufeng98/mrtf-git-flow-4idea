package com.github.xiaolyuh.ui;

import com.github.xiaolyuh.utils.ConfigUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import kotlin.Pair;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class GitFlowToolWindowComp extends JComponent {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPanel mainPanel;
    private JButton hideBtn;
    private JButton availableBtn;
    private JButton button1;

    public GitFlowToolWindowComp(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        Pair<String, String> pair = ConfigUtil.getKubesphereUser();
        usernameField.setText(pair.getFirst());
        passwordField.setText(pair.getSecond());

        hideBtn.addActionListener(e -> toolWindow.hide());
        availableBtn.addActionListener(e -> toolWindow.setAvailable(false));
        button1.addActionListener(e -> {
            ToolWindowManager.getInstance(project).notifyByBalloon("GitflowPlus.toolWindow",
                    MessageType.INFO, "<h1>hello world</h1>");
        });
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }
}
