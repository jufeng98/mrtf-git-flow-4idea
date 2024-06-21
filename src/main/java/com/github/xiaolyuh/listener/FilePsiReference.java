package com.github.xiaolyuh.listener;

import com.github.xiaolyuh.utils.StringUtils;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class FilePsiReference extends PsiReferenceBase<PsiLiteralExpression> {

    public FilePsiReference(@NotNull PsiLiteralExpression element, boolean soft) {
        super(element, soft);
    }

    @Override
    public @NotNull TextRange getAbsoluteRange() {
        return super.getAbsoluteRange();
    }

    @Override
    public @Nullable PsiElement resolve() {
        Project project = myElement.getProject();
        String fileName = (String) myElement.getValue();
        if (StringUtils.isBlank(fileName)) {
            return null;
        }

        Collection<VirtualFile> files = FilenameIndex.getVirtualFilesByName(fileName, GlobalSearchScope.projectScope(project));
        if (files.isEmpty()) {
            return null;
        }

        VirtualFile virtualFile = files.iterator().next();

        return PsiManager.getInstance(project).findFile(virtualFile);
    }

    @Override
    public Object @NotNull [] getVariants() {
        return super.getVariants();
    }
}
