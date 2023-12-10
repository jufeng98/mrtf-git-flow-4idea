package com.github.xiaolyuh.ui;

import com.github.xiaolyuh.utils.ConfigUtil;
import com.intellij.ui.components.JBPasswordField;
import kotlin.Pair;

import javax.swing.*;

public class KubesphereDialog {
    private JPanel mainPanel;
    private JTextField nameField;
    private JBPasswordField passwordField;

    public KubesphereDialog() {
        fillCurKubesphereUser();
    }

    public void fillCurKubesphereUser() {
        Pair<String, String> pair = ConfigUtil.getKubesphereUser();
        nameField.setText(pair.getFirst());
        passwordField.setText(pair.getSecond());
    }

    public Pair<String, String> getCurKubesphereUser() {
        return new Pair<>(nameField.getText(), new String(passwordField.getPassword()));
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }
}
