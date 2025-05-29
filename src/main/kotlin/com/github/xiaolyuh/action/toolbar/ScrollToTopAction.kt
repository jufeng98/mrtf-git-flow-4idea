package com.github.xiaolyuh.action.toolbar

import com.github.xiaolyuh.i18n.I18n
import com.github.xiaolyuh.icons.GitFlowPlusIcons
import com.intellij.openapi.actionSystem.AnActionEvent

/**
 * @author yudong
 */
class ScrollToTopAction : LogBaseAction(I18n.nls("scroll.top"), GitFlowPlusIcons.scrollUp) {

    override fun actionPerformed(e: AnActionEvent) {
        val consoleView = getConsoleView(e)

        consoleView.scrollTo(0)
    }

}
