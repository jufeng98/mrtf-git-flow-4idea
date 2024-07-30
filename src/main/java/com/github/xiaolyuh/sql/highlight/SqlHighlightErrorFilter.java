package com.github.xiaolyuh.sql.highlight;

import com.github.xiaolyuh.sql.SqlLanguage;
import com.intellij.codeInsight.highlighting.HighlightErrorFilter;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.xml.XmlText;
import org.jetbrains.annotations.NotNull;

public class SqlHighlightErrorFilter extends HighlightErrorFilter {
    @Override
    public boolean shouldHighlightErrorElement(@NotNull PsiErrorElement element) {
        if (element.getLanguage() == SqlLanguage.INSTANCE) {
            // 忽略MyBatis xml里的sql语法错误
            PsiLanguageInjectionHost injectionHost = InjectedLanguageManager.getInstance(element.getProject()).getInjectionHost(element);
            return !(injectionHost instanceof XmlText);
        }

        return true;
    }
}
