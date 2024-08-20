package com.github.xiaolyuh.suppress

import com.github.xiaolyuh.http.psi.HttpOrdinaryContent
import com.intellij.codeInspection.InspectionSuppressor
import com.intellij.codeInspection.SuppressQuickFix
import com.intellij.json.JsonLanguage
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.PsiElement

class MyInspectionSuppressor : InspectionSuppressor {
    override fun isSuppressedFor(element: PsiElement, toolId: String): Boolean {
        val language = element.language
        if (language === JsonLanguage.INSTANCE) {
            val injectionHost = InjectedLanguageManager.getInstance(element.project).getInjectionHost(element)
            return injectionHost is HttpOrdinaryContent
        }

        return false
    }

    override fun getSuppressActions(element: PsiElement?, toolId: String): Array<SuppressQuickFix> {
        return arrayOf()
    }
}
