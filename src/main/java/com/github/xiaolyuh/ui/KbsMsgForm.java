package com.github.xiaolyuh.ui;

import com.github.xiaolyuh.action.toolbar.LiveLogAction;
import com.github.xiaolyuh.action.toolbar.LoadMoreAction;
import com.github.xiaolyuh.action.toolbar.RefreshLogAction;
import com.github.xiaolyuh.action.toolbar.ScrollToEndAction;
import com.github.xiaolyuh.action.toolbar.ScrollToTopAction;
import com.github.xiaolyuh.action.toolbar.SoftWrapAction;
import com.github.xiaolyuh.service.ExecutorService;
import com.github.xiaolyuh.service.KubesphereService;
import com.github.xiaolyuh.utils.NotifyUtil;
import com.github.xiaolyuh.utils.VirtualFileUtils;
import com.google.common.collect.Lists;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.util.DocumentUtil;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.Future;

public class KbsMsgForm extends JComponent implements Disposable {
    private String newInstanceName;
    private Pair<byte[], byte[]> pair;
    private final Project project;
    private String selectService;
    private boolean previews;

    private JPanel mainPanel;
    private JBTabbedPane jTabbedPane;
    private JPanel toolbarPanel;

    private Future<?> insRefreshFuture;
    private int tailLines = 500;
    private Editor editor;
    private final List<Editor> releaseEditorList = Lists.newArrayList();


    public KbsMsgForm(Pair<byte[], byte[]> pair, Project project) {
        this.pair = pair;
        this.project = project;

        ActionToolbar toolbar = createSimpleToolbar();

        toolbarPanel.add(toolbar.getComponent());

        fillEditorWithErrorTxt();
    }

    public KbsMsgForm(byte[] textBytes, Project project, String selectService, String newInstanceName, boolean previews) {
        this.newInstanceName = newInstanceName;
        this.project = project;
        this.selectService = selectService;
        this.previews = previews;

        ActionToolbar toolbar = createToolbar();

        toolbarPanel.add(toolbar.getComponent());

        fillEditorWithRunningTxt(textBytes, false);
    }

    public void openLiveLog() {
        insRefreshFuture = refreshInsRunningData();
    }

    public void closeLiveLog() {
        if (insRefreshFuture == null) {
            return;
        }

        insRefreshFuture.cancel(true);
        insRefreshFuture = null;
    }

    private @NotNull ActionToolbar createSimpleToolbar() {
        DefaultActionGroup defaultActionGroup = new DefaultActionGroup();

        SoftWrapAction softWrapAction = new SoftWrapAction();
        ScrollToTopAction scrollToTopAction = new ScrollToTopAction();
        ScrollToEndAction scrollToEndAction = new ScrollToEndAction();


        defaultActionGroup.add(softWrapAction);
        defaultActionGroup.addSeparator();
        defaultActionGroup.add(scrollToTopAction);
        defaultActionGroup.add(scrollToEndAction);

        ActionManager actionManager = ActionManager.getInstance();
        ActionToolbar toolbar = actionManager.createActionToolbar("gitFlowPlusLogVerticalToolbar", defaultActionGroup, false);
        toolbar.setTargetComponent(this);
        return toolbar;
    }

    private @NotNull ActionToolbar createToolbar() {
        DefaultActionGroup defaultActionGroup = new DefaultActionGroup();

        SoftWrapAction softWrapAction = new SoftWrapAction();
        ScrollToTopAction scrollToTopAction = new ScrollToTopAction();
        ScrollToEndAction scrollToEndAction = new ScrollToEndAction();
        LiveLogAction liveLogAction = new LiveLogAction();
        RefreshLogAction refreshLogAction = new RefreshLogAction();
        LoadMoreAction loadMoreAction = new LoadMoreAction();

        liveLogAction.initAction(refreshLogAction, loadMoreAction);

        defaultActionGroup.add(softWrapAction);
        defaultActionGroup.addSeparator();
        defaultActionGroup.add(scrollToTopAction);
        defaultActionGroup.add(scrollToEndAction);
        defaultActionGroup.addSeparator();
        defaultActionGroup.add(liveLogAction);
        defaultActionGroup.addSeparator();
        defaultActionGroup.add(refreshLogAction);
        defaultActionGroup.add(loadMoreAction);

        ActionManager actionManager = ActionManager.getInstance();
        ActionToolbar toolbar = actionManager.createActionToolbar("gitFlowPlusLogVerticalToolbar", defaultActionGroup, false);
        toolbar.setTargetComponent(this);
        return toolbar;
    }

    @Override
    public void dispose() {
        closeLiveLog();

        EditorFactory editorFactory = EditorFactory.getInstance();
        releaseEditorList.forEach(editorFactory::releaseEditor);
    }

    public void refreshLogData() {
        new Task.Backgroundable(project, "Loading......", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                byte[] textBytes;
                try {
                    KubesphereService kubesphereService = KubesphereService.Companion.getInstance(project);
                    textBytes = kubesphereService.getContainerStartInfo(selectService, newInstanceName, tailLines,
                            previews, false);
                } catch (Exception e) {
                    //noinspection CallToPrintStackTrace
                    e.printStackTrace();

                    NotifyUtil.notifyError(project, "出错了:" + ExceptionUtils.getStackTrace(e));
                    return;
                }
                ApplicationManager.getApplication().invokeLater(() -> fillEditorWithRunningTxt(textBytes, false));
            }
        }.queue();
    }

    private Future<?> refreshInsRunningData() {
        ExecutorService executorService = ExecutorService.Companion.getInstance(project);

        return executorService.addTask(() -> {
            try {
                KubesphereService kubesphereService = KubesphereService.Companion.getInstance(project);
                kubesphereService.getContainerStartInfo(selectService, newInstanceName,
                        1000, false, true, body -> SwingUtilities
                                .invokeLater(() -> ApplicationManager.getApplication()
                                        .invokeLater(() -> fillEditorWithRunningTxt(body, true))
                                ));
            } catch (Exception e) {
                //noinspection CallToPrintStackTrace
                e.printStackTrace();

                NotifyUtil.notifyInfo(project, "实时刷新出错了:" + ExceptionUtils.getStackTrace(e));
            }
        });
    }

    private void fillEditorWithErrorTxt() {
        addTab("compile", pair.getSecond());
        addTab("push", pair.getFirst());

        jTabbedPane.setSelectedIndex(0);

        editor = releaseEditorList.get(0);

        jTabbedPane.addChangeListener(e -> {
            int selectedIndex = jTabbedPane.getSelectedIndex();
            editor = releaseEditorList.get(selectedIndex);
        });
    }

    private void addTab(String tabTitle, byte[] txtBytes) {
        editor = createTextEditorAndSetText(project, txtBytes);

        jTabbedPane.addTab(tabTitle, editor.getComponent());
    }

    private void fillEditorWithRunningTxt(byte[] txtBytes, boolean append) {
        DocumentUtil.writeInRunUndoTransparentAction(() -> {
            if (editor != null) {
                Document document = editor.getDocument();

                String txt = new String(txtBytes, StandardCharsets.UTF_8);

                String res = txt.replace("\r", "");

                if (append) {
                    document.setText(document.getText() + res);
                } else {
                    document.setText(res);
                }

                scrollToBottom();

                return;
            }

            addTab("容器日志", txtBytes);

            jTabbedPane.setSelectedIndex(0);
        });
    }

    private Editor createTextEditorAndSetText(Project project, byte[] txtBytes) {
        VirtualFile virtualFile = VirtualFileUtils.INSTANCE.createLogVirtualFileFromText(txtBytes);

        PsiManager psiManager = PsiManager.getInstance(project);
        PsiFile psiFile = psiManager.findFile(virtualFile);

        @SuppressWarnings("DataFlowIssue")
        Document document = PsiDocumentManager.getInstance(project).getDocument(psiFile);

        EditorFactory editorFactory = EditorFactory.getInstance();
        @SuppressWarnings("DataFlowIssue")
        Editor editor = editorFactory.createEditor(document, project, virtualFile, true);

        releaseEditorList.add(editor);

        return editor;
    }

    public void scrollToBottom() {
        Caret caret = editor.getCaretModel().getPrimaryCaret();

        caret.moveToOffset(editor.getDocument().getTextLength());

        editor.getScrollingModel().scrollToCaret(ScrollType.RELATIVE);
    }

    public void increaseTailLines() {
        tailLines += 500;
    }

    public JComponent getMainPanel() {
        return mainPanel;
    }

    public Editor getEditor() {
        return editor;
    }

    public Project getProject() {
        return project;
    }
}
