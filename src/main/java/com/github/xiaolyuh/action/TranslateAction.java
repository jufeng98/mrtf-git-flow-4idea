package com.github.xiaolyuh.action;

import com.github.xiaolyuh.ui.TranslateDialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.Disposer;
import org.jetbrains.annotations.NotNull;

public class TranslateAction extends AnAction {

    @Override
    public void update(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        Editor editor = event.getData(CommonDataKeys.EDITOR);

        // 只有打开了编辑器且有选中内容时才显示 TranslateAction 菜单
        boolean showAction = project != null && editor != null && editor.getSelectionModel().hasSelection();
        event.getPresentation().setEnabledAndVisible(showAction);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getRequiredData(CommonDataKeys.PROJECT);
        Editor editor = event.getRequiredData(CommonDataKeys.EDITOR);

        // 取得选中的内容文本
        Caret primaryCaret = editor.getCaretModel().getPrimaryCaret();
        int start = primaryCaret.getSelectionStart();
        int end = primaryCaret.getSelectionEnd();
        String selectedText = primaryCaret.getSelectedText();


        TranslateDialog translateDialog = new TranslateDialog(selectedText);
        // 将搭好的窗体 TranslateDialog 传入 JBPopupFactory
        JBPopup popup = JBPopupFactory.getInstance()
                .createComponentPopupBuilder(translateDialog.createCenterPanel(), null).createPopup();
        // 由 JBPopup 决定窗体显示的最合适位置
        popup.showInBestPositionFor(editor);
        Disposer.register(Disposer.newDisposable(), popup);

        // 取得编辑器的文档对象
        Document document = editor.getDocument();

        translateDialog.addReplaceResultListener(replaceVal -> {
            // 使用翻译后的文本替换选中的文本
            WriteCommandAction.runWriteCommandAction(project, () ->
                    document.replaceString(start, end, replaceVal)
            );
            // 取消文本选中
            primaryCaret.removeSelection();
            popup.closeOk(null);
        });
    }
}
