package com.github.xiaolyuh.listener;

import com.github.xiaolyuh.http.env.EnvFileService;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiUtil;
import org.jetbrains.annotations.NotNull;

public class FileEditorListener implements FileEditorManagerListener {
    private static final Key<EnvFileDocumentListener> KEY = Key.create("gitflowplus.envFileListener");

    @Override
    public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        Project project = source.getProject();
        String fileName = file.getName();

        if (!fileName.equals(EnvFileService.ENV_FILE_NAME) && !fileName.equals(EnvFileService.PRIVATE_ENV_FILE_NAME)) {
            return;
        }

        PsiFile psiFile = PsiUtil.getPsiFile(project, file);
        Document document = PsiDocumentManager.getInstance(project).getDocument(psiFile);
        if (document == null) {
            return;
        }

        EnvFileDocumentListener listener = new EnvFileDocumentListener(project, fileName);
        file.putUserData(KEY, listener);
        document.addDocumentListener(listener);
    }

    @Override
    public void fileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        Project project = source.getProject();
        if (!file.getName().equals(EnvFileService.ENV_FILE_NAME) && !file.getName().equals(EnvFileService.PRIVATE_ENV_FILE_NAME)) {
            return;
        }

        PsiFile psiFile = PsiUtil.getPsiFile(project, file);
        EnvFileDocumentListener listener = file.getUserData(KEY);
        if (listener == null) {
            return;
        }

        Document document = PsiDocumentManager.getInstance(project).getDocument(psiFile);
        if (document == null) {
            return;
        }

        document.removeDocumentListener(listener);
    }

    private static class EnvFileDocumentListener implements DocumentListener {
        private final Project project;
        private final String fileName;

        public EnvFileDocumentListener(Project project, String fileName) {
            this.project = project;
            this.fileName = fileName;
        }

        @Override
        public void documentChanged(@NotNull DocumentEvent event) {
            EnvFileService envFileService = EnvFileService.Companion.getService(project);

            envFileService.initEnv(fileName, event.getDocument().getCharsSequence());
        }
    }
}
