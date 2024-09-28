package com.github.xiaolyuh.http.gutter

import com.github.xiaolyuh.http.action.HttpAction
import com.github.xiaolyuh.http.psi.HttpTypes
import com.github.xiaolyuh.utils.HttpUtils
import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiUtil
import com.intellij.psi.util.elementType

class HttpRunLineMarkerContributor : RunLineMarkerContributor() {

    override fun getInfo(element: PsiElement): Info? {
        if (element.elementType != HttpTypes.POST) {
            return null
        }

        val virtualFile = PsiUtil.getVirtualFile(element)
        if (HttpUtils.isFileInIdeaDir(virtualFile)) {
            return null
        }

        val action = ActionManager.getInstance().getAction("GitFlowPlus.HttpAction") as HttpAction

        return Info(AllIcons.Actions.Execute, arrayOf<AnAction>(action)) { _ ->
            "执行请求"
        }
    }

}
