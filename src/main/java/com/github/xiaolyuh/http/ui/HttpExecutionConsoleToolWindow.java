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
import com.intellij.psi.PsiFile;
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
import java.util.Objects;

public class HttpExecutionConsoleToolWindow implements Disposable {
    public JPanel mainPanel;

    private JPanel responsePanel;
    private JPanel requestPanel;

    private final List<Editor> editorList = Lists.newArrayList();

    public void initPanelData(HttpInfo httpInfo, Throwable throwable, String tabName, Project project, Disposable parentDisposer) {
        Disposer.register(parentDisposer, this);

        GridLayoutManager layout = (GridLayoutManager) requestPanel.getParent().getLayout();
        GridConstraints constraints = layout.getConstraintsForComponent(requestPanel);

        if (throwable != null) {
            String msg = ExceptionUtils.getStackTrace(throwable);
            JComponent jComponent = createEditor(msg.getBytes(StandardCharsets.UTF_8), "error.log", project, tabName);
            requestPanel.add(jComponent, constraints);
            return;
        }

        boolean imageType = Objects.equals(httpInfo.getType(), "image");
        Exception httpException = httpInfo.getHttpException();
        boolean hasError = httpException != null;

        byte[] reqBytes;
        if (imageType || hasError) {
            List<String> list = Lists.newArrayList();
            list.addAll(httpInfo.getHttpReqDescList());
            list.addAll(httpInfo.getHttpResDescList());
            reqBytes = String.join("", list).getBytes(StandardCharsets.UTF_8);
        } else {
            reqBytes = String.join("", httpInfo.getHttpReqDescList()).getBytes(StandardCharsets.UTF_8);
        }

        JComponent reqComponent = createEditor(reqBytes, "req.http", project, tabName);
        requestPanel.add(reqComponent, constraints);

        if (hasError) {
            String msg = ExceptionUtils.getStackTrace(httpException);
            JComponent jComponent = createEditor(msg.getBytes(StandardCharsets.UTF_8), "error.log", project, tabName);
            responsePanel.add(jComponent, constraints);
            return;
        }

        if (imageType) {
            try {
                @SuppressWarnings("DataFlowIssue")
                @Cleanup
                ByteArrayInputStream inputStream = new ByteArrayInputStream(httpInfo.getByteArray());
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
        JComponent resComponent = createEditor(resBytes, "res.http", project, tabName);
        responsePanel.add(resComponent, constraintsRes);
    }

    public JComponent createEditor(byte[] bytes, String suffix, Project project, String tabName) {
        VirtualFile virtualFile = VirtualFileUtils.createHttpVirtualFileFromText(bytes, suffix, project, tabName);
        PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(project);
        PsiFile psiFile = PsiUtil.getPsiFile(project, virtualFile);
        Document document = psiDocumentManager.getDocument(psiFile);

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
