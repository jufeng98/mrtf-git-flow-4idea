package com.github.xiaolyuh.sql.livetempate;

import com.github.xiaolyuh.sql.SqlFileType;
import com.github.xiaolyuh.sql.parser.SqlFile;
import com.intellij.codeInsight.template.FileTypeBasedContextType;
import com.intellij.codeInsight.template.TemplateActionContext;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

public class SqlTemplateContextType extends FileTypeBasedContextType {
    protected SqlTemplateContextType() {
        super("gfp-sql", "SQL", SqlFileType.INSTANCE);
    }

    @Override
    public boolean isInContext(@NotNull TemplateActionContext templateActionContext) {
        PsiFile psiFile = templateActionContext.getFile();
        return psiFile instanceof SqlFile;
    }

}
