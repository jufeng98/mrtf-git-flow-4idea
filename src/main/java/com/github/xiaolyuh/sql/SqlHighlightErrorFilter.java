package com.github.xiaolyuh.sql;

import com.intellij.codeInsight.highlighting.HighlightErrorFilter;
import com.intellij.psi.PsiErrorElement;
import org.jetbrains.annotations.NotNull;

public class SqlHighlightErrorFilter extends HighlightErrorFilter {
    @Override
    public boolean shouldHighlightErrorElement(@NotNull PsiErrorElement element) {
        return element.getLanguage() != SqlLanguage.INSTANCE;
    }
}
