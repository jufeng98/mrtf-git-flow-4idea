package com.github.xiaolyuh.http.inject

import com.github.xiaolyuh.http.psi.HttpOrdinaryContent
import com.github.xiaolyuh.http.psi.HttpScript
import com.github.xiaolyuh.http.psi.HttpTypes
import com.intellij.json.JsonLanguage
import com.intellij.lang.Language
import com.intellij.lang.injection.MultiHostInjector
import com.intellij.lang.injection.MultiHostRegistrar
import com.intellij.lang.java.JShellLanguage
import com.intellij.lang.xml.XMLLanguage
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiLanguageInjectionHost
import com.intellij.psi.util.elementType

class HttpInjectionContributor : MultiHostInjector {

    override fun getLanguagesToInject(registrar: MultiHostRegistrar, context: PsiElement) {
        var language: Language? = null
        var textRange: TextRange? = null
        val elementType = context.firstChild.elementType
        if (elementType == HttpTypes.JSON_TEXT) {
            language = JsonLanguage.INSTANCE
            textRange = innerRange(context)
        } else if (elementType == HttpTypes.XML_TEXT) {
            language = XMLLanguage.INSTANCE
            textRange = innerRange(context)
        } else if (context is HttpScript) {
            language = JShellLanguage.INSTANCE
            textRange = innerRangeScript(context)
        }

        if (language == null || textRange == null) {
            return
        }

        registrar.startInjecting(language)
        registrar.addPlace(null, null, context as PsiLanguageInjectionHost, textRange)
        registrar.doneInjecting()
    }

    override fun elementsToInjectIn(): MutableList<out Class<out PsiElement>> {
        return mutableListOf(HttpOrdinaryContent::class.java, HttpScript::class.java)
    }

    private fun innerRange(context: PsiElement): TextRange? {
        val textRange = context.textRange
        val textRangeTmp = textRange.shiftLeft(textRange.startOffset)
        if (textRangeTmp.endOffset == 0) {
            return null
        }

        return textRangeTmp
    }

    private fun innerRangeScript(context: PsiElement): TextRange? {
        var textRange = context.textRange
        textRange = textRange.shiftLeft(textRange.startOffset)
        if (textRange.endOffset == 0) {
            return null
        }

        val textRangeTmp = TextRange(textRange.startOffset + 4, textRange.endOffset - 2)
        if (textRangeTmp.endOffset == 0) {
            return null
        }

        return textRangeTmp
    }
}
