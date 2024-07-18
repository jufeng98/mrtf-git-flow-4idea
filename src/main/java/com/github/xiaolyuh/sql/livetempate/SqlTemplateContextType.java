package com.github.xiaolyuh.sql.livetempate;

import com.dbn.language.common.psi.LeafPsiElement;
import com.dbn.language.common.psi.PsiUtil;
import com.github.xiaolyuh.sql.SqlLanguage;
import com.github.xiaolyuh.sql.highlight.SqlSyntaxHighlighter;
import com.intellij.codeInsight.template.TemplateActionContext;
import com.intellij.codeInsight.template.TemplateContextType;
import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SqlTemplateContextType extends TemplateContextType {
    protected SqlTemplateContextType() {
        super("GFP-SQL", "SQL (GitFlowPlus)");
    }

    @Override
    public boolean isInContext(@NotNull TemplateActionContext templateActionContext) {
        PsiFile psiFile = templateActionContext.getFile();
        int startOffset = templateActionContext.getStartOffset();
        Language language = psiFile.getLanguage();
        if (!(language instanceof SqlLanguage)) {
            return false;
        }

        LeafPsiElement<?> leafPsiElement = PsiUtil.lookupLeafBeforeOffset(psiFile, startOffset);
        return leafPsiElement == null;
    }

    @Nullable
    @Override
    public SyntaxHighlighter createHighlighter() {
        return new SqlSyntaxHighlighter();
    }
}
