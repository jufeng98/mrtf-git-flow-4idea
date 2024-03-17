package com.github.xiaolyuh.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ArthasCommandAction extends AnAction {

    @Override
    public void update(@NotNull AnActionEvent event) {
        event.getPresentation().setEnabledAndVisible(false);

        PsiMethod psiMethod = findPsiMethod(event);

        event.getPresentation().setEnabledAndVisible(psiMethod != null);
    }

    @Nullable
    private PsiMethod findPsiMethod(AnActionEvent event) {
        Project project = event.getProject();
        Editor editor = event.getData(CommonDataKeys.EDITOR);
        event.getPresentation().setEnabledAndVisible(false);

        if (project == null || editor == null) {
            return null;
        }

        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        if (psiFile == null) {
            return null;
        }

        if (!(psiFile instanceof PsiJavaFile)) {
            return null;
        }

        PsiElement element = psiFile.findElementAt(editor.getCaretModel().getCurrentCaret().getOffset());
        if (element == null) {
            return null;
        }

        PsiElement parent = element.getParent();
        if (parent == null) {
            return null;
        }

        if (!(parent instanceof PsiMethod)) {
            return null;
        }

        return (PsiMethod) parent;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        PsiMethod psiMethod = findPsiMethod(event);
        if (psiMethod == null) {
            return;
        }

//        Query<PsiMethod> psiMethods = OverridingMethodsSearch.search(psiMethod);

//        PsiClass psiClass = PsiTreeUtil.getParentOfType(psiMethod, PsiClass.class);
        PsiClass psiClass = psiMethod.getContainingClass();
        if (psiClass == null) {
            return;
        }

        String className = psiClass.getQualifiedName();

        String methodName = psiMethod.getName();

        Messages.showInfoMessage(event.getProject(), "watch " + className + " " + methodName + " {params} -x 2 -b", "标题");
    }
}
