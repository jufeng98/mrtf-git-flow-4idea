package com.github.xiaolyuh.http.gutter

import com.github.xiaolyuh.http.action.HttpAction
import com.github.xiaolyuh.http.psi.HttpMethod
import com.github.xiaolyuh.utils.HttpUtils
import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.icons.AllIcons
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiUtil

class HttpRunLineMarkerContributor : RunLineMarkerContributor() {

    override fun getInfo(element: PsiElement): Info? {
        val parent = element.parent
        if (parent !is HttpMethod) {
            return null
        }

        val virtualFile = PsiUtil.getVirtualFile(element)
        if (HttpUtils.isFileInIdeaDir(virtualFile)) {
            return null
        }

        val action = HttpAction(parent)

        return Info(AllIcons.Actions.Execute, arrayOf(action)) { _ -> "执行请求" }
    }

}
