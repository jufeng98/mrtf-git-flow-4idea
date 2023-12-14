package com.github.xiaolyuh.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.Pair;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class KbsErrorDialog extends DialogWrapper {
    private JPanel panel1;
    private JTextArea textAreaPush;
    private JTextArea textAreaCompile;

    public KbsErrorDialog(String title,Pair<String, String> pair, Project project) {
        super(project);
        setTitle(title);
        init();

        textAreaPush.setText(pair.getSecond());
        textAreaCompile.setText(pair.getFirst());
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return panel1;
    }
}
