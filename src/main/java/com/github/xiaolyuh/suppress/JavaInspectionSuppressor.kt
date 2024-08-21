package com.github.xiaolyuh.suppress

import com.github.xiaolyuh.http.psi.HttpScript
import com.intellij.codeInspection.InspectionSuppressor
import com.intellij.codeInspection.SuppressQuickFix
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.PsiElement

class JavaInspectionSuppressor : InspectionSuppressor {
    override fun isSuppressedFor(element: PsiElement, toolId: String): Boolean {
        val injectionHost = InjectedLanguageManager.getInstance(element.project).getInjectionHost(element)
        return injectionHost is HttpScript
    }

    override fun getSuppressActions(element: PsiElement?, toolId: String): Array<SuppressQuickFix> {
        return arrayOf()
    }
}
