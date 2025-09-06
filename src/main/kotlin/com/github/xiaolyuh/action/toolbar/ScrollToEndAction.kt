package com.github.xiaolyuh.action.toolbar

import com.github.xiaolyuh.i18n.I18n
import com.github.xiaolyuh.icons.GitFlowPlusIcons
import com.intellij.openapi.actionSystem.AnActionEvent

/**
 * @author yudong
 */
class ScrollToEndAction : LogBaseAction(I18n.nls("scroll.end"), GitFlowPlusIcons.scrollDown) {

    override fun actionPerformed(e: AnActionEvent) {
        val consoleView = getConsoleView(e)

        consoleView.requestScrollingToEnd()
    }

}
