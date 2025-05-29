package com.github.xiaolyuh.action.toolbar

import com.github.xiaolyuh.i18n.I18n
import com.github.xiaolyuh.icons.GitFlowPlusIcons
import com.intellij.execution.impl.ConsoleViewImpl
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.ScrollType

/**
 * @author yudong
 */
class ScrollToEndAction : LogBaseAction(I18n.nls("scroll.end"), GitFlowPlusIcons.scrollDown) {

    override fun actionPerformed(e: AnActionEvent) {
        val consoleView = getConsoleView(e) as ConsoleViewImpl
        val editor = consoleView.editor!!

        val caretModel = editor.caretModel
        caretModel.moveToOffset(editor.document.textLength)

        editor.scrollingModel.scrollToCaret(ScrollType.CENTER)
    }

}
