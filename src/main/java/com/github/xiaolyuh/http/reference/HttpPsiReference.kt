package com.github.xiaolyuh.http.reference

import com.github.xiaolyuh.http.psi.HttpUrl
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase

class HttpPsiReference(httpUrl: HttpUrl, private val searchTxt: String, rangeInElement: TextRange) :
    PsiReferenceBase<HttpUrl>(httpUrl, rangeInElement) {

    override fun resolve(): PsiElement {
        return HttpFakePsiElement(myElement, searchTxt)
    }

}
