package com.github.xiaolyuh.http.reference

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase

class HttpVariablePsiReference(element: PsiElement, private val variableName: String, rangeInElement: TextRange) :
    PsiReferenceBase<PsiElement>(element, rangeInElement) {

    override fun resolve(): PsiElement {
        return HttpVariableFakePsiElement(element, variableName)
    }

}
