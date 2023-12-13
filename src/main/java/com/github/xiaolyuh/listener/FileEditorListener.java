package com.github.xiaolyuh.listener;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;

public class FileEditorListener implements FileEditorManagerListener {
    @Override
    public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
//        PsiJavaFile psiFile = (PsiJavaFile) PsiManager.getInstance(source.getProject()).findFile(file);
//        psiFile.accept(new JavaRecursiveElementVisitor() {
//            @Override
//            public void visitField(PsiField field) {
//                System.out.println(field);
//            }
//        });
    }
}
