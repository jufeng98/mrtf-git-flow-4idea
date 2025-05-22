package com.github.xiaolyuh.action.toolbar

import com.github.xiaolyuh.icons.GitFlowPlusIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.ScrollType

/**
 * @author yudong
 */
class ScrollToEndAction : LogBaseAction("滚动到底部", GitFlowPlusIcons.SCROLL_DOWN) {

    override fun actionPerformed(e: AnActionEvent) {
        val editor = getEditor(e)

        val caretModel = editor.caretModel
        caretModel.moveToOffset(editor.document.textLength)

        editor.scrollingModel.scrollToCaret(ScrollType.CENTER)
    }

}
