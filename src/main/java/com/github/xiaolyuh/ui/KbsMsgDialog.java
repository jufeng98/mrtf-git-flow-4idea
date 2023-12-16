package com.github.xiaolyuh.ui;

import com.github.xiaolyuh.utils.ExecutorUtils;
import com.github.xiaolyuh.utils.KubesphereUtils;
import com.github.xiaolyuh.utils.VirtualFileUtils;
import com.google.common.collect.Lists;
import com.intellij.find.EditorSearchSession;
import com.intellij.find.SearchReplaceComponent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.fileEditor.impl.text.TextEditorProvider;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ArrayUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.util.List;

public class KbsMsgDialog extends DialogWrapper {
    private String newInstanceName;
    private Pair<byte[], byte[]> pair;
    private final Project project;
    private String selectService;
    private String runsUrl;

    private JPanel mainPanel;
    private JTabbedPane jTabbedPane;
    private JButton refreshBtn;
    private JButton topBtn;
    private JButton bottomBtn;
    private JButton swBtn;
    private JButton insRefreshBtn;

    private volatile boolean insRefreshOpen = false;
    private int tailLines = 500;
    private TextEditor textEditor;
    private final List<Editor> releaseEditorList = Lists.newArrayList();

    public KbsMsgDialog(String title, Pair<byte[], byte[]> pair, Project project) {
        super(project);
        this.pair = pair;
        this.project = project;
        setTitle(title);
        init();

        mainPanel.remove(insRefreshBtn);
        mainPanel.remove(swBtn);
        mainPanel.remove(topBtn);
        mainPanel.remove(refreshBtn);
        mainPanel.remove(bottomBtn);

        fillEditorWithErrorTxt();
    }

    public KbsMsgDialog(String title, byte[] textBytes, Project project, String selectService, String runsUrl,
                        String newInstanceName, boolean previews) {
        super(project);
        setTitle(title);
        this.newInstanceName = newInstanceName;
        this.project = project;
        this.selectService = selectService;
        this.runsUrl = runsUrl;
        init();

        refreshBtn.addActionListener(e -> {
            tailLines += tailLines;
            refreshRunningData(tailLines, previews);
        });

        insRefreshBtn.addActionListener(e -> {
            //noinspection NonAtomicOperationOnVolatileField
            insRefreshOpen = !insRefreshOpen;
            String tip = insRefreshOpen ? "关闭实时刷新" : "开启实时刷新";
            insRefreshBtn.setText(tip);
            refreshBtn.setEnabled(!insRefreshOpen);
            refreshInsRunningData(KbsMsgDialog.this);
        });

        swBtn.addActionListener(e -> {
            EditorSettings settings = textEditor.getEditor().getSettings();
            boolean useSoftWraps = settings.isUseSoftWraps();
            String tip = useSoftWraps ? "开启软换行" : "关闭软换行";
            swBtn.setText(tip);
            settings.setUseSoftWraps(!useSoftWraps);
        });

        topBtn.addActionListener(e -> scrollToTop(textEditor.getEditor()));

        bottomBtn.addActionListener(e -> scrollToBottom(textEditor.getEditor()));

        fillEditorWithRunningTxt(project, textBytes, false);
    }

    @Override
    protected void dispose() {
        insRefreshOpen = false;
        releaseEditorList.forEach(editor -> EditorFactory.getInstance().releaseEditor(editor));
        super.dispose();
    }

    private void refreshRunningData(int tailLines, boolean previews) {
        Task.Modal task = new Task.Modal(project, mainPanel, "Loading......", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                byte[] textBytes = KubesphereUtils.getContainerStartInfo(runsUrl, selectService, newInstanceName, tailLines,
                        previews, false);
                ApplicationManager.getApplication().invokeLater(() -> fillEditorWithRunningTxt(project, textBytes, false));
            }
        };
        ProgressManager.getInstance().run(task);
    }

    private void refreshInsRunningData(KbsMsgDialog kbsMsgDialog) {
        ExecutorUtils.addTask(() -> KubesphereUtils.getContainerStartInfo(runsUrl, selectService, newInstanceName,
                1000, false, true, body -> SwingUtilities
                        .invokeLater(() -> ApplicationManager.getApplication()
                                .invokeLater(() -> fillEditorWithRunningTxt(project, body, true))), kbsMsgDialog));
    }

    private void fillEditorWithErrorTxt() {
        addTab("compile", pair.getSecond());
        addTab("push", pair.getFirst());
    }

    private void addTab(String tabTitle, byte[] txtBytes) {
        TextEditor textEditor = convertTxtToEditor(project, txtBytes);

        JPanel panel = new JPanel(new BorderLayout());
        jTabbedPane.addTab(tabTitle, panel);

        JComponent editorComp = textEditor.getComponent();
        panel.add(editorComp, BorderLayout.CENTER);

        EditorSearchSession editorSearchSession = EditorSearchSession.start(textEditor.getEditor(), project);
        SearchReplaceComponent findComp = editorSearchSession.getComponent();
        panel.add(findComp, BorderLayout.NORTH);
    }

    private void fillEditorWithRunningTxt(Project project, byte[] txtBytes, boolean append) {
        ApplicationManager.getApplication().runWriteAction(() -> {
            if (textEditor != null) {
                try {
                    if (append) {
                        InputStream inputStream = textEditor.getFile().getInputStream();
                        byte[] bytes = inputStream.readAllBytes();
                        inputStream.close();
                        byte[] newBytes = ArrayUtil.mergeArrays(bytes, txtBytes);
                        textEditor.getFile().setBinaryContent(newBytes);
                    } else {
                        textEditor.getFile().setBinaryContent(txtBytes);
                    }
                    scrollToBottom(textEditor.getEditor());
                } catch (Throwable ignored) {

                }
                return;
            }
            textEditor = convertTxtToEditor(project, txtBytes);

            jTabbedPane.addTab("容器日志", textEditor.getComponent());
        });
    }

    private TextEditor convertTxtToEditor(Project project, byte[] txtBytes) {
        final TextEditor[] textEditors = new TextEditor[1];
        WriteAction.runAndWait(() -> {
            VirtualFile virtualFile = VirtualFileUtils.createVirtualFileFromText(txtBytes);

            TextEditor textEditor = (TextEditor) TextEditorProvider.getInstance().createEditor(project, virtualFile);
            textEditor.getEditor().getDocument().setReadOnly(true);

            scrollToBottom(textEditor.getEditor());

            textEditors[0] = textEditor;

            releaseEditorList.add(textEditor.getEditor());
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

    public boolean getInsRefreshOpen() {
        return insRefreshOpen;
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return mainPanel;
    }
}
