package com.github.xiaolyuh.suppress;

import com.github.xiaolyuh.http.psi.HttpOrdinaryContent;
import com.github.xiaolyuh.http.psi.HttpScript;
import com.github.xiaolyuh.sql.SqlLanguage;
import com.intellij.codeInsight.highlighting.HighlightErrorFilter;
import com.intellij.json.JsonLanguage;
import com.intellij.lang.Language;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.lang.java.JavaLanguage;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.xml.XmlText;
import org.jetbrains.annotations.NotNull;

public class MyHighlightErrorFilter extends HighlightErrorFilter {
    @Override
    public boolean shouldHighlightErrorElement(@NotNull PsiErrorElement element) {
        Language language = element.getLanguage();
        Project project = element.getProject();

        if (language == SqlLanguage.INSTANCE) {
            // 忽略MyBatis xml里的sql语法错误
            PsiLanguageInjectionHost injectionHost = InjectedLanguageManager.getInstance(project).getInjectionHost(element);
            return !(injectionHost instanceof XmlText);
        }

        if (language == JsonLanguage.INSTANCE) {
            PsiLanguageInjectionHost injectionHost = InjectedLanguageManager.getInstance(project).getInjectionHost(element);
            return !(injectionHost instanceof HttpOrdinaryContent);
        }

        if (language == JavaLanguage.INSTANCE) {
            PsiLanguageInjectionHost injectionHost = InjectedLanguageManager.getInstance(project).getInjectionHost(element);
            return !(injectionHost instanceof HttpScript);
        }

        return true;
    }
}
