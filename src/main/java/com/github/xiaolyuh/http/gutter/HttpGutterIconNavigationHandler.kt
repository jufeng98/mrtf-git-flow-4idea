package com.github.xiaolyuh.http.gutter

import com.intellij.codeInsight.daemon.GutterIconNavigationHandler
import com.intellij.psi.PsiElement
import java.awt.event.MouseEvent

class HttpGutterIconNavigationHandler : GutterIconNavigationHandler<PsiElement> {
    override fun navigate(e: MouseEvent, elt: PsiElement) {
        println(e)
        println(elt)
    }
}
