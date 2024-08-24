package com.github.xiaolyuh.http.ui;

import com.github.xiaolyuh.http.service.EnvFileService;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;

import javax.swing.*;
import java.util.Set;

public class HttpEditorTopForm extends JComponent {
    public static final Key<HttpEditorTopForm> KEY = Key.create("gitflowplus.httpEditorTopForm");
    private JComboBox<String> envComboBox;
    public JPanel mainPanel;

    public void initData(EnvFileService envFileService) {
        Set<String> presetEnvSet = envFileService.getPresetEnvList();
        presetEnvSet.forEach(it -> envComboBox.addItem(it));
    }

    public String getSelectedEnv() {
        return (String) envComboBox.getSelectedItem();
    }

    @SuppressWarnings("DataFlowIssue")
    public static String getSelectedEnv(Project project) {
        FileEditorManager editorManager = FileEditorManager.getInstance(project);
        FileEditor selectedEditor = editorManager.getSelectedEditor();

        HttpEditorTopForm httpEditorTopForm = selectedEditor.getUserData(HttpEditorTopForm.KEY);

        return httpEditorTopForm.getSelectedEnv();
    }

}
