package com.github.xiaolyuh.http.inject

import com.github.xiaolyuh.http.psi.HttpOrdinaryContent
import com.github.xiaolyuh.http.psi.HttpTypes
import com.intellij.json.JsonLanguage
import com.intellij.lang.Language
import com.intellij.lang.injection.MultiHostInjector
import com.intellij.lang.injection.MultiHostRegistrar
import com.intellij.lang.xml.XMLLanguage
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiLanguageInjectionHost
import com.intellij.psi.util.elementType

class HttpInjectionContributor : MultiHostInjector {

    override fun getLanguagesToInject(registrar: MultiHostRegistrar, context: PsiElement) {
        var language: Language? = null
        val elementType = context.firstChild.elementType
        if (elementType == HttpTypes.JSON_TEXT) {
            language = JsonLanguage.INSTANCE
        } else if (elementType == HttpTypes.XML_TEXT) {
            language = XMLLanguage.INSTANCE
        }

        if (language == null) {
            return
        }

        registrar.startInjecting(language)
        val textRange = innerRangeStrippingQuotes(context) ?: return
        registrar.addPlace(null, null, context as PsiLanguageInjectionHost, textRange)
        registrar.doneInjecting()
    }

    override fun elementsToInjectIn(): MutableList<out Class<out PsiElement>> {
        return mutableListOf(HttpOrdinaryContent::class.java)
    }

    private fun innerRangeStrippingQuotes(context: PsiElement): TextRange? {
        val textRange = context.textRange
        val textRangeTmp = textRange.shiftLeft(textRange.startOffset)
        if (textRangeTmp.endOffset == 0) {
            return null
        }

        return textRangeTmp
    }
}
