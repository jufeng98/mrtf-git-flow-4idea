package com.github.xiaolyuh.cls;

import com.github.xiaolyuh.cls.step2.SimpleLanguage;
import com.intellij.json.psi.JsonStringLiteral;
import com.intellij.lang.injection.MultiHostInjector;
import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.lang.injection.general.Injection;
import com.intellij.lang.injection.general.LanguageInjectionContributor;
import com.intellij.lang.injection.general.SimpleInjection;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

final class SimpleLanguageInjectionContributor implements LanguageInjectionContributor, MultiHostInjector {

    @Override
    public @Nullable Injection getInjection(@NotNull PsiElement context) {
        if (!(context instanceof JsonStringLiteral)) {
            return null;
        }
        JsonStringLiteral jsonStringLiteral = (JsonStringLiteral) context;
        String value = jsonStringLiteral.getValue();
        if (!value.startsWith("simple:")) {
            return null;
        }
        return new SimpleInjection(SimpleLanguage.INSTANCE, "", "", null);
    }

    @Override
    public void getLanguagesToInject(@NotNull MultiHostRegistrar registrar,
                                     @NotNull PsiElement context) {
        if (!(context instanceof JsonStringLiteral)) {
            return;
        }
        JsonStringLiteral jsonStringLiteral = (JsonStringLiteral) context;
        String value = jsonStringLiteral.getValue();
        if (!value.startsWith("simple:")) {
            return;
        }
        registrar
                .startInjecting(SimpleLanguage.INSTANCE)
                .addPlace(null, null,
                        (PsiLanguageInjectionHost) context,
                        innerRangeStrippingQuotes(jsonStringLiteral))
                .doneInjecting();
    }

    private TextRange innerRangeStrippingQuotes(JsonStringLiteral jsonStringLiteral) {
        TextRange textRange = jsonStringLiteral.getTextRange();
        TextRange textRange1 = textRange.shiftLeft(textRange.getStartOffset());
        return new TextRange(textRange1.getStartOffset() + 1, textRange1.getEndOffset() - 1);
    }

    @Override
    public @NotNull List<? extends Class<? extends PsiElement>> elementsToInjectIn() {
        return List.of(JsonStringLiteral.class);
    }

}
