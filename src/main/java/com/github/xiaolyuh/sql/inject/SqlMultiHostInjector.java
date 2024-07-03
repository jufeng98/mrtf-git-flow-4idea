package com.github.xiaolyuh.sql.inject;

import com.github.xiaolyuh.sql.SqlLanguage;
import com.intellij.lang.injection.MultiHostInjector;
import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlText;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class SqlMultiHostInjector implements MultiHostInjector {

    @Override
    public void getLanguagesToInject(@NotNull MultiHostRegistrar registrar, @NotNull PsiElement context) {
        if (!shouldInject(context)) {
            return;
        }

        registrar.startInjecting(SqlLanguage.INSTANCE);
        XmlTag xmlTag = (XmlTag) context;

        boolean hasAddPlace = false;
        Collection<XmlText> collection = PsiTreeUtil.findChildrenOfType(xmlTag, XmlText.class);
        for (XmlText xmlText : collection) {
            TextRange textRange = innerRangeStrippingQuotes(xmlText);
            if (textRange == null) {
                continue;
            }

            registrar.addPlace(null, null, (PsiLanguageInjectionHost) xmlText, textRange);
            hasAddPlace = true;
        }

        if (hasAddPlace) {
            registrar.doneInjecting();
        }
    }

    @Override
    public @NotNull List<? extends Class<? extends PsiElement>> elementsToInjectIn() {
        return List.of(XmlTag.class);
    }

    private boolean shouldInject(PsiElement context) {
        if (!(context instanceof XmlTag)) {
            return false;
        }

        XmlTag xmlTag = (XmlTag) context;
        String name = xmlTag.getName();
        return "select".equals(name) || "delete".equals(name) || "update".equals(name) || "insert".equals(name);
    }


    private TextRange innerRangeStrippingQuotes(PsiElement context) {
        TextRange textRange = context.getTextRange();
        TextRange textRangeTmp = textRange.shiftLeft(textRange.getStartOffset());
        if (textRangeTmp.getEndOffset() == 0) {
            return null;
        }

        return textRangeTmp;
    }
}
