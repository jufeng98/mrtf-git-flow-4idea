package com.github.xiaolyuh.http.ui;

import com.github.xiaolyuh.http.HttpInfo;
import com.github.xiaolyuh.utils.VirtualFileUtils;
import com.google.common.collect.Lists;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.util.PsiUtil;
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
import java.util.List;

public class HttpExecutionConsoleToolWindow implements Disposable {
    public JPanel mainPanel;

    private JPanel responsePanel;
    private JPanel requestPanel;

    private final List<Editor> editorList = Lists.newArrayList();

    public void initPanelData(HttpInfo httpInfo, Throwable throwable, Project project, Disposable parentDisposer) {
        Disposer.register(parentDisposer, this);

        GridLayoutManager layout = (GridLayoutManager) requestPanel.getParent().getLayout();
        GridConstraints constraints = layout.getConstraintsForComponent(requestPanel);

        if (throwable != null) {
            String msg = ExceptionUtils.getStackTrace(throwable);
            JComponent jComponent = createEditor(msg.getBytes(StandardCharsets.UTF_8), "log", project);
            requestPanel.add(jComponent, constraints);
            return;
        }

        byte[] reqBytes = String.join("", httpInfo.getHttpReqDescList())
                .getBytes(StandardCharsets.UTF_8);

        JComponent reqComponent = createEditor(reqBytes, "req.http", project);
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

        GridLayoutManager layoutRes = (GridLayoutManager) responsePanel.getParent().getLayout();
        GridConstraints constraintsRes = layoutRes.getConstraintsForComponent(responsePanel);
        JComponent resComponent = createEditor(resBytes, "res.http", project);
        responsePanel.add(resComponent, constraintsRes);
    }

    public JComponent createEditor(byte[] bytes, String suffix, Project project) {
        VirtualFile virtualFile = VirtualFileUtils.createHttpVirtualFileFromText(bytes, suffix, project);
        Document document = PsiDocumentManager.getInstance(project).getDocument(PsiUtil.getPsiFile(project, virtualFile));

        EditorFactory editorFactory = EditorFactory.getInstance();
        @SuppressWarnings("DataFlowIssue")
        Editor editor = editorFactory.createEditor(document, project, virtualFile, true);
        editorList.add(editor);

        return editor.getComponent();
    }

    @Override
    public void dispose() {
        EditorFactory editorFactory = EditorFactory.getInstance();
        editorList.forEach(editorFactory::releaseEditor);
    }
}
