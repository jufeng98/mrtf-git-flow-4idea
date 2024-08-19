package com.github.xiaolyuh.http.gutter

import com.dbn.utils.TooltipUtils.showTooltip
import com.github.xiaolyuh.http.HttpRequestEnum
import com.github.xiaolyuh.http.psi.HttpBody
import com.github.xiaolyuh.http.psi.HttpHeaders
import com.github.xiaolyuh.http.psi.HttpMethod
import com.github.xiaolyuh.http.psi.HttpUrl
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.awt.event.MouseEvent

class HttpGutterIconNavigationHandler(val element: HttpMethod) : GutterIconNavigationHandler<PsiElement> {
    override fun navigate(e: MouseEvent, elt: PsiElement) {
        val httpUrl = PsiTreeUtil.getNextSiblingOfType(element, HttpUrl::class.java)
        if (httpUrl == null) {
            showTooltip("url不正确!", element.project)
            return
        }

        val httpHeaders = PsiTreeUtil.getNextSiblingOfType(httpUrl, HttpHeaders::class.java)

        val httpBody = PsiTreeUtil.getNextSiblingOfType(httpUrl, HttpBody::class.java)

        val httpRequestEnum = HttpRequestEnum.getInstance(element)

        CoroutineScope(Dispatchers.IO).launch  {
            val resObj: Any = httpRequestEnum.executeAsync(httpUrl, httpHeaders, httpBody)
            println(resObj)
        }

    }
}
