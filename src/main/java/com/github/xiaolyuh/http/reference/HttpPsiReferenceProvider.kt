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

        return createSqlReferences(httpUrl)
    }


    private fun createSqlReferences(httpUrl: HttpUrl): Array<PsiReference> {
        val module = HttpUtils.getOriginalModule(httpUrl) ?: return arrayOf()
        val pair = HttpUtils.getSearchTxtInfo(httpUrl, module) ?: return arrayOf()
        val searchTxt = pair.first
        val textRange = pair.second

        val httpPsiReference = HttpPsiReference(httpUrl, searchTxt, textRange)

        return arrayOf(httpPsiReference)
    }
}
