package com.github.xiaolyuh.action.toolbar

import com.github.xiaolyuh.support.MyActionButton
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.actionSystem.impl.ActionButton
import com.intellij.ui.JBColor
import javax.swing.JComponent

/**
 * @author yudong
 */
class LiveLogAction : LogBaseAction("实时日志", AllIcons.General.Web) {
    private lateinit var actionButton: ActionButton
    private var liveOpen = false

    private lateinit var refreshLogAction: RefreshLogAction
    private lateinit var loadMoreAction: LoadMoreAction

    override fun createCustomComponent(presentation: Presentation, place: String): JComponent {
        actionButton = super.createCustomComponent(presentation, place) as MyActionButton

        return actionButton
    }

    fun initAction(refreshLogAction: RefreshLogAction, loadMoreAction: LoadMoreAction) {
        this.refreshLogAction = refreshLogAction
        this.loadMoreAction = loadMoreAction
    }

    private fun changeActionButton() {
        if (liveOpen) {
            actionButton.background = JBColor.LIGHT_GRAY
        } else {
            actionButton.background = null
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        liveOpen = !liveOpen

        val form = getKbsMsgForm(e)

        refreshLogAction.switchEnabled()
        loadMoreAction.switchEnabled()

        if (liveOpen) {
            form.openLiveLog()
        } else {
            form.closeLiveLog()
        }

        changeActionButton()
    }
}
