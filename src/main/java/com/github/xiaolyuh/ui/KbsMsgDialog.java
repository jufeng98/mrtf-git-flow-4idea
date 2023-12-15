package com.github.xiaolyuh.ui;

import com.github.xiaolyuh.utils.KubesphereUtils;
import com.github.xiaolyuh.utils.VirtualFileUtils;
import com.intellij.find.EditorSearchSession;
import com.intellij.find.SearchReplaceComponent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.fileEditor.ex.FileEditorProviderManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class KbsMsgDialog extends DialogWrapper {
    private JPanel mainPanel;
    private JTabbedPane jTabbedPane;
    private JButton refreshBtn;
    private JButton topBtn;
    private JButton bottomBtn;
    private JButton swBtn;
    private int tailLines = 300;
    private TextEditor textEditor;

    public KbsMsgDialog(String title, Pair<String, String> pair, Project project) {
        super(project);
        setTitle(title);
        init();

        mainPanel.remove(swBtn);
        mainPanel.remove(topBtn);
        mainPanel.remove(refreshBtn);
        mainPanel.remove(bottomBtn);

        fillEditorWithErrorTxt(project, pair);
    }

    public KbsMsgDialog(String title, String msg, Project project, String selectService, String runsUrl,
                        String newInstanceName, boolean previews) {
        super(project);

        setTitle(title);
        init();

        refreshBtn.addActionListener(e -> {
            tailLines += tailLines;
            refreshRunningData(project, runsUrl, selectService, newInstanceName, tailLines, previews);
        });

        swBtn.addActionListener(e -> {
            EditorSettings settings = textEditor.getEditor().getSettings();
            boolean useSoftWraps = settings.isUseSoftWraps();
            settings.setUseSoftWraps(!useSoftWraps);
        });

        topBtn.addActionListener(e -> scrollToTop(textEditor.getEditor()));

        bottomBtn.addActionListener(e -> scrollToBottom(textEditor.getEditor()));

        fillEditorWithRunningTxt(project, msg);
    }

    private void refreshRunningData(Project project, String runsUrl, String selectService, String newInstanceName,
                                    int tailLines, boolean previews) {
        Task.Modal task = new Task.Modal(project, mainPanel, "Loading......", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                String msg = KubesphereUtils.getContainerStartInfo(runsUrl, selectService, newInstanceName, tailLines,
                        previews, false);
                ApplicationManager.getApplication().invokeLater(() -> {
                    fillEditorWithRunningTxt(project, msg);
                });
            }
        };
        ProgressManager.getInstance().run(task);
    }

    private void fillEditorWithErrorTxt(Project project, Pair<String, String> pair) {
        addTab("compile", project, pair.getSecond());
        addTab("push", project, pair.getFirst());
    }

    private void addTab(String tabTitle, Project project, String txt) {
        TextEditor textEditor = convertTxtToEditor(project, txt);

        JPanel panel = new JPanel(new BorderLayout());
        jTabbedPane.addTab(tabTitle, panel);

        JComponent editorComp = textEditor.getComponent();
        panel.add(editorComp, BorderLayout.CENTER);

        EditorSearchSession editorSearchSession = EditorSearchSession.start(textEditor.getEditor(), project);
        SearchReplaceComponent findComp = editorSearchSession.getComponent();
        panel.add(findComp, BorderLayout.NORTH);
    }

    private void fillEditorWithRunningTxt(Project project, String txt) {
        WriteAction.run(() -> {
            if (textEditor != null) {
                try {
                    textEditor.getFile().setBinaryContent(txt.getBytes(StandardCharsets.UTF_8));
                    scrollToBottom(textEditor.getEditor());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return;
            }
            textEditor = convertTxtToEditor(project, txt);

            JPanel panel = new JPanel(new BorderLayout());
            jTabbedPane.addTab("容器日志", panel);

            JComponent editorComp = textEditor.getComponent();
            panel.add(editorComp, BorderLayout.CENTER);

            EditorSearchSession editorSearchSession = EditorSearchSession.start(textEditor.getEditor(), project);
            SearchReplaceComponent findComp = editorSearchSession.getComponent();
            panel.add(findComp, BorderLayout.NORTH);
        });
    }

    private TextEditor convertTxtToEditor(Project project, String txt) {
        final TextEditor[] textEditors = new TextEditor[1];
        WriteAction.runAndWait(() -> {
            VirtualFile virtualFile = VirtualFileUtils.createVirtualFileFromText(txt);
            FileEditorProvider provider = FileEditorProviderManager.getInstance().getProviders(project, virtualFile)[0];
            TextEditor textEditor = (TextEditor) provider.createEditor(project, virtualFile);

            scrollToBottom(textEditor.getEditor());

            textEditors[0] = textEditor;
        });
        return textEditors[0];
    }

    private void scrollToTop(@NotNull Editor editor) {
        Caret caret = editor.getCaretModel().getPrimaryCaret();
        caret.moveToOffset(0);
        editor.getScrollingModel().scrollToCaret(ScrollType.RELATIVE);
    }

    private void scrollToBottom(@NotNull Editor editor) {
        Caret caret = editor.getCaretModel().getPrimaryCaret();
        caret.moveToOffset(editor.getDocument().getTextLength());
        editor.getScrollingModel().scrollToCaret(ScrollType.RELATIVE);
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return mainPanel;
    }
}
