package com.github.xiaolyuh.ui;

import com.github.xiaolyuh.utils.ExecutorUtils;
import com.github.xiaolyuh.utils.KubesphereUtils;
import com.github.xiaolyuh.utils.NotifyUtil;
import com.github.xiaolyuh.utils.VirtualFileUtils;
import com.google.common.collect.Lists;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.fileEditor.impl.text.TextEditorProvider;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ArrayUtil;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Future;

public class KbsMsgDialog extends DialogWrapper {
    private String newInstanceName;
    private Pair<byte[], byte[]> pair;
    private final Project project;
    private String selectService;

    private JPanel mainPanel;
    private JTabbedPane jTabbedPane;
    private JButton loadMoreBtn;
    private JButton topBtn;
    private JButton bottomBtn;
    private JButton swBtn;
    private JButton insRefreshBtn;
    private JButton refreshButton;
    private JPanel btnPanel;

    private boolean insRefreshOpen = false;
    private Future<?> insRefreshFuture;
    private int tailLines = 500;
    private TextEditor textEditor;
    private final List<TextEditor> releaseEditorList = Lists.newArrayList();

    {
        setOKButtonText("关闭");
        setModal(false);
    }

    public KbsMsgDialog(String title, Pair<byte[], byte[]> pair, Project project) {
        super(project);
        this.pair = pair;
        this.project = project;
        setTitle(title);
        init();

        fitScreen();

        btnPanel.remove(insRefreshBtn);
        btnPanel.remove(swBtn);
        btnPanel.remove(topBtn);
        btnPanel.remove(loadMoreBtn);
        btnPanel.remove(bottomBtn);
        btnPanel.remove(refreshButton);

        fillEditorWithErrorTxt();
    }

    public KbsMsgDialog(String title, byte[] textBytes, Project project, String selectService,
                        String newInstanceName, boolean previews) {
        super(project);
        setTitle(title);
        this.newInstanceName = newInstanceName;
        this.project = project;
        this.selectService = selectService;
        init();

        fitScreen();

        loadMoreBtn.addActionListener(e -> {
            tailLines += 500;
            refreshRunningData(tailLines, previews);
        });

        refreshButton.addActionListener(e -> refreshRunningData(tailLines, previews));

        insRefreshBtn.addActionListener(e -> {
            insRefreshOpen = !insRefreshOpen;
            String tip = insRefreshOpen ? "关闭实时日志" : "开启实时日志";
            insRefreshBtn.setText(tip);
            loadMoreBtn.setEnabled(!insRefreshOpen);
            refreshButton.setEnabled(!insRefreshOpen);
            if (!insRefreshOpen) {
                insRefreshFuture.cancel(true);
                insRefreshFuture = null;
                return;
            }
            insRefreshFuture = refreshInsRunningData();
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

    private void fitScreen() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension dimension = new Dimension(screenSize.width - 60, 600);
        mainPanel.setPreferredSize(dimension);

        Dimension minDimension = new Dimension(screenSize.width - 60, 200);
        mainPanel.setMinimumSize(minDimension);
    }

    @Override
    protected Action @NotNull [] createActions() {
        return new Action[]{getOKAction()};
    }

    @Override
    protected @Nullable ActionListener createCancelAction() {
        return null;
    }

    @Override
    protected void dispose() {
        super.dispose();
        insRefreshOpen = false;
        if (insRefreshFuture != null) {
            insRefreshFuture.cancel(true);
        }
        releaseEditorList.forEach(editor -> {
            String filePath = editor.getFile().getPath();
            Disposer.dispose(editor);
            try {
                Files.delete(Path.of(filePath));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void refreshRunningData(int tailLines, boolean previews) {
        Task.Modal task = new Task.Modal(project, mainPanel, "Loading......", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                byte[] textBytes;
                try {
                    textBytes = KubesphereUtils.getContainerStartInfo(project, selectService, newInstanceName, tailLines,
                            previews, false);
                } catch (Exception e) {
                    NotifyUtil.notifyError(project, "出错了:" + ExceptionUtils.getStackTrace(e));
                    return;
                }
                ApplicationManager.getApplication().invokeLater(() -> fillEditorWithRunningTxt(project, textBytes, false));
            }
        };
        ProgressManager.getInstance().run(task);
    }

    private Future<?> refreshInsRunningData() {
        return ExecutorUtils.addTask(() -> {
            try {
                KubesphereUtils.getContainerStartInfo(project, selectService, newInstanceName,
                        1000, false, true, body -> SwingUtilities
                                .invokeLater(() -> ApplicationManager.getApplication()
                                        .invokeLater(() -> fillEditorWithRunningTxt(project, body, true))
                                ));
            } catch (Exception e) {
                NotifyUtil.notifyInfo(project, "实时刷新出错了:" + ExceptionUtils.getStackTrace(e));
            }
        });
    }

    private void fillEditorWithErrorTxt() {
        addTab("compile", pair.getSecond());
        addTab("push", pair.getFirst());
    }

    private void addTab(String tabTitle, byte[] txtBytes) {
        TextEditor textEditor = createTextEditorAndSetText(project, txtBytes);
        jTabbedPane.addTab(tabTitle, textEditor.getComponent());
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
            textEditor = createTextEditorAndSetText(project, txtBytes);

            jTabbedPane.addTab("容器日志", textEditor.getComponent());
        });
    }

    private TextEditor createTextEditorAndSetText(Project project, byte[] txtBytes) {
        final TextEditor[] textEditors = new TextEditor[1];
        WriteAction.runAndWait(() -> {
            VirtualFile virtualFile = VirtualFileUtils.createVirtualFileFromText(txtBytes);

            TextEditor textEditor = (TextEditor) TextEditorProvider.getInstance().createEditor(project, virtualFile);
            Editor editor = textEditor.getEditor();

            editor.getDocument().setReadOnly(true);

            scrollToBottom(editor);

            textEditors[0] = textEditor;

            releaseEditorList.add(textEditor);
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
