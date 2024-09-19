package com.github.xiaolyuh.http.gutter

import com.github.xiaolyuh.http.psi.HttpMethod
import com.github.xiaolyuh.utils.HttpUtils
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.icons.AllIcons
import com.intellij.navigation.GotoRelatedItem
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiUtil
import com.intellij.util.ConstantFunction

class HttpLineMarkerProvider : RelatedItemLineMarkerProvider() {
    override fun collectNavigationMarkers(
        element: PsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>>,
    ) {
        if (element !is HttpMethod) {
            return
        }

        val virtualFile = PsiUtil.getVirtualFile(element)
        if (HttpUtils.isFileInIdeaDir(virtualFile)) {
            return
        }

        val navigationHandler = HttpGutterIconNavigationHandler(element)

        val lineMarkerInfo = RelatedItemLineMarkerInfo(
            element.firstChild,
            element.getTextRange(),
            AllIcons.Actions.Execute,
            ConstantFunction("执行请求"),
            navigationHandler,
            GutterIconRenderer.Alignment.CENTER
        ) {
            GotoRelatedItem.createItems(
                emptyList<PsiElement>()
            )
        }

        result.add(lineMarkerInfo)
    }
}
