package com.github.xiaolyuh.http.reference

import com.github.xiaolyuh.http.psi.HttpUrl
import com.github.xiaolyuh.utils.HttpUtils
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.util.ProcessingContext

class HttpPsiReferenceProvider : PsiReferenceProvider() {
    override fun getReferencesByElement(
        element: PsiElement,
        context: ProcessingContext,
    ): Array<PsiReference> {
        val httpUrl = element as HttpUrl

        return createReferences(httpUrl)
    }


    private fun createReferences(httpUrl: HttpUrl): Array<PsiReference> {
        val virtualFile = HttpUtils.getOriginalFile(httpUrl) ?: return arrayOf()

        val path = virtualFile.parent?.path ?: return arrayOf()

        val pair = HttpUtils.getSearchTxtInfo(httpUrl, path) ?: return arrayOf()

        val searchTxt = pair.first
        val textRange = pair.second

        val httpPsiReference = HttpPsiReference(httpUrl, searchTxt, textRange)

        return arrayOf(httpPsiReference)
    }
}
