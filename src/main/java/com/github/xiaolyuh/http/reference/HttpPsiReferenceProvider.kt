package com.github.xiaolyuh.http.reference

import com.github.xiaolyuh.http.psi.HttpUrl
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.util.ProcessingContext
import java.net.URI

class HttpPsiReferenceProvider : PsiReferenceProvider() {
    override fun getReferencesByElement(
        element: PsiElement,
        context: ProcessingContext,
    ): Array<PsiReference> {
        val httpUrl = element as HttpUrl

        return createSqlReferences(httpUrl)
    }


    private fun createSqlReferences(httpUrl: HttpUrl): Array<PsiReference> {
        val url = httpUrl.text

        val start: Int
        val bracketIdx = url.lastIndexOf("}")
        start = if (bracketIdx != -1) {
            bracketIdx + 1
        } else {
            var tmpIdx: Int
            val uri: URI
            try {
                uri = URI(url)
                tmpIdx = url.indexOf("/", url.replace(uri.path, "").length)
            } catch (e: Exception) {
                tmpIdx = url.indexOf("/")
            }
            tmpIdx
        }

        if (start == -1) {
            return arrayOf()
        }

        val idx = url.lastIndexOf("?")
        val end = if (idx == -1) {
            url.length
        } else {
            idx
        }

        val textRange = TextRange(start, end)
        val searchTxt = url.substring(start, end)
        val httpPsiReference = HttpPsiReference(httpUrl, searchTxt, textRange)

        return arrayOf(httpPsiReference)
    }
}
