package com.github.xiaolyuh.ui;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaCodeFragmentFactory;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpressionCodeFragment;
import com.intellij.psi.PsiFile;
import com.intellij.ui.EditorTextField;

import javax.swing.*;
import java.awt.*;

public class EditorDialog extends DialogWrapper {
    private JPanel mainPanel;
    private JPanel contentPanel;

    protected EditorDialog(Project project, VirtualFile virtualFile) {
        super(project);

        init();

        TextEditor editor = (TextEditor) FileEditorManager.getInstance(project).getEditors(virtualFile)[0];

        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getEditor().getDocument());
        PsiElement element = psiFile.findElementAt(editor.getEditor().getCaretModel().getOffset());

        PsiExpressionCodeFragment code =
                JavaCodeFragmentFactory.getInstance(project)
                        .createExpressionCodeFragment("", element, null, true);

        Document document =
                PsiDocumentManager.getInstance(project).getDocument(code);

        EditorTextField editorTextField =
                new EditorTextField(document, project, JavaFileType.INSTANCE);
        contentPanel.add(editorTextField, BorderLayout.CENTER);

    }

    @Override
    protected JComponent createCenterPanel() {
        return mainPanel;
    }
}
