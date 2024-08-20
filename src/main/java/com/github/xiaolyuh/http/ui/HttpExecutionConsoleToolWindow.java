package com.github.xiaolyuh.http.ui;

import com.github.xiaolyuh.utils.VirtualFileUtils;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.fileEditor.impl.text.TextEditorProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import kotlin.Pair;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class HttpExecutionConsoleToolWindow {
    public JPanel mainPanel;

    private JPanel bodyPanel;

    private JTextArea headerTextArea;

    public TextEditor initPanelData(Pair<List<String>, Object> resPair, Throwable throwable, Project project) {
        if (throwable != null) {
            String exceptionMsg = ExceptionUtils.getStackTrace(throwable);
            headerTextArea.append(exceptionMsg);
            return null;
        }

        List<String> component1 = resPair.component1();
        for (String res : component1) {
            headerTextArea.append(res);
        }

        TextEditor textEditor = null;
        JComponent jComponent;
        Object obj = resPair.component2();
        if (obj instanceof BufferedImage) {
            BufferedImage bufferedImage = (BufferedImage) obj;
            jComponent = new JLabel(new ImageIcon(bufferedImage));
        } else {
            @SuppressWarnings("unchecked")
            Pair<String, byte[]> pair = (Pair<String, byte[]>) obj;
            VirtualFile virtualFile = VirtualFileUtils.createVirtualFileFromText(pair.getSecond(), pair.getFirst());
            textEditor = (TextEditor) TextEditorProvider.getInstance().createEditor(project, virtualFile);
            Editor editor = textEditor.getEditor();
            editor.getDocument().setReadOnly(true);
            jComponent = editor.getComponent();
        }

        GridLayoutManager layout = (GridLayoutManager) bodyPanel.getParent().getLayout();
        GridConstraints constraints = layout.getConstraintsForComponent(bodyPanel);
        bodyPanel.add(jComponent, constraints);

        return textEditor;
    }
}
