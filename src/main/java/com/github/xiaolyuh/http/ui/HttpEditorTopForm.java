package com.github.xiaolyuh.http.ui;

import com.github.xiaolyuh.http.env.EnvFileService;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.net.URL;
import java.util.Objects;
import java.util.Set;

public class HttpEditorTopForm extends JComponent {
    public static final Key<HttpEditorTopForm> KEY = Key.create("gitflowplus.httpEditorTopForm");
    private JComboBox<String> envComboBox;
    public JPanel mainPanel;
    private JComboBox<String> exampleComboBox;
    private Project project;

    public HttpEditorTopForm() {
        exampleComboBox.addActionListener(e -> {
            ClassLoader classLoader = getClass().getClassLoader();
            Object selectedItem = exampleComboBox.getSelectedItem();

            URL url = null;
            if (Objects.equals(selectedItem, "GET requests")) {
                url = classLoader.getResource("examples/get-requests.http");
            } else if (Objects.equals(selectedItem, "POST requests")) {
                url = classLoader.getResource("examples/post-requests.http");
            } else if (Objects.equals(selectedItem, "Request with Authorization")) {
                url = classLoader.getResource("examples/requests-with-authorization.http");
            } else if (Objects.equals(selectedItem, "Request with tests and Scripts")) {
                url = classLoader.getResource("examples/requests-with-scripts.http");
            } else if (Objects.equals(selectedItem, "Response presentations")) {
                url = classLoader.getResource("examples/responses-presentation.http");
            }

            if (url != null) {
                VirtualFile virtualFile = VfsUtil.findFileByURL(url);
                //noinspection DataFlowIssue
                FileEditorManager.getInstance(project).openFile(virtualFile, true);
                exampleComboBox.setSelectedIndex(0);
            }
        });
    }

    public void initEnvCombo(Module module, String httpFileParentPath) {
        project = module.getProject();

        EnvFileService envFileService = EnvFileService.Companion.getService(project);
        Set<String> presetEnvSet = envFileService.getPresetEnvList(httpFileParentPath);

        presetEnvSet.forEach(it -> envComboBox.addItem(it));
    }

    public String getSelectedEnv() {
        return (String) envComboBox.getSelectedItem();
    }

    public void setSelectEnv(String env) {
        int itemCount = envComboBox.getItemCount();
        for (int i = 0; i < itemCount; i++) {
            String item = envComboBox.getItemAt(i);
            if (!Objects.equals(item, env)) {
                continue;
            }

            envComboBox.setSelectedIndex(i);
            break;
        }
    }

    @SuppressWarnings("DataFlowIssue")
    public static @Nullable String getSelectedEnv(Project project) {
        FileEditorManager editorManager = FileEditorManager.getInstance(project);
        FileEditor selectedEditor = editorManager.getSelectedEditor();

        HttpEditorTopForm httpEditorTopForm = selectedEditor.getUserData(HttpEditorTopForm.KEY);

        return httpEditorTopForm.getSelectedEnv();
    }

    @SuppressWarnings("DataFlowIssue")
    public static void setSelectedEnv(String httpFilePath, Project project, String env) {
        FileEditorManager editorManager = FileEditorManager.getInstance(project);
        FileEditor selectedEditor = editorManager.getSelectedEditor();
        if (selectedEditor == null) {
            return;
        }

        VirtualFile virtualFile = selectedEditor.getFile();
        if (virtualFile == null || !Objects.equals(httpFilePath, virtualFile.getPath())) {
            return;
        }

        HttpEditorTopForm httpEditorTopForm = selectedEditor.getUserData(HttpEditorTopForm.KEY);

        httpEditorTopForm.setSelectEnv(env);
    }

}
