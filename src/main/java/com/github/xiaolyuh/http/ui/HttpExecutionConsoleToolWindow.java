package com.github.xiaolyuh.http.ui;

import com.github.xiaolyuh.http.HttpInfo;
import com.github.xiaolyuh.utils.VirtualFileUtils;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.fileEditor.impl.text.TextEditorProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import lombok.Cleanup;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class HttpExecutionConsoleToolWindow {
    public JPanel mainPanel;

    private JPanel responsePanel;
    private JPanel requestPanel;

    public void initPanelData(HttpInfo httpInfo, Throwable throwable, Project project, Disposable parentDisposer) {
        GridLayoutManager layout = (GridLayoutManager) requestPanel.getParent().getLayout();
        GridConstraints constraints = layout.getConstraintsForComponent(requestPanel);

        if (throwable != null) {
            JTextArea jTextArea = new JTextArea(ExceptionUtils.getStackTrace(throwable));
            requestPanel.add(jTextArea, constraints);
            return;
        }

        byte[] reqBytes = String.join("", httpInfo.getHttpReqDescList())
                .getBytes(StandardCharsets.UTF_8);

        JComponent reqComponent = createEditor(reqBytes, project, parentDisposer);
        requestPanel.add(reqComponent, constraints);

        if (httpInfo.getType().equals("image")) {
            try {
                @Cleanup
                ByteArrayInputStream inputStream = new ByteArrayInputStream((httpInfo.getByteArray()));
                BufferedImage bufferedImage = ImageIO.read(inputStream);
                JLabel jComponent = new JLabel(new ImageIcon(bufferedImage));
                responsePanel.add(jComponent, constraints);
                return;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        byte[] resBytes = String.join("", httpInfo.getHttpResDescList())
                .getBytes(StandardCharsets.UTF_8);

        GridLayoutManager layout1 = (GridLayoutManager) responsePanel.getParent().getLayout();
        GridConstraints constraints1 = layout1.getConstraintsForComponent(responsePanel);
        JComponent resComponent = createEditor(resBytes, project, parentDisposer);
        responsePanel.add(resComponent, constraints1);
    }

    public JComponent createEditor(byte[] bytes, Project project, Disposable parentDisposer) {
        VirtualFile virtualFile = VirtualFileUtils.createVirtualFileFromText(bytes, "http");
        TextEditor textEditor = (TextEditor) TextEditorProvider.getInstance().createEditor(project, virtualFile);
        Disposer.register(parentDisposer, textEditor);
        Editor editor = textEditor.getEditor();
        editor.getDocument().setReadOnly(true);
        return editor.getComponent();
    }

}
