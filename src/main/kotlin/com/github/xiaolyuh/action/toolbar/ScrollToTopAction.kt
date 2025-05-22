package com.github.xiaolyuh.action.toolbar

import com.github.xiaolyuh.icons.GitFlowPlusIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.ScrollType

/**
 * @author yudong
 */
class ScrollToTopAction : LogBaseAction("滚动到顶部", GitFlowPlusIcons.SCROLL_UP) {

    override fun actionPerformed(e: AnActionEvent) {
        val editor = getEditor(e)

        val caretModel = editor.caretModel
        caretModel.moveToOffset(0)

        editor.scrollingModel.scrollToCaret(ScrollType.CENTER)
    }

}
