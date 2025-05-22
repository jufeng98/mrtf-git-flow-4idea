package com.github.xiaolyuh.action.toolbar

import com.github.xiaolyuh.support.MyActionButton
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.actionSystem.impl.ActionButton
import java.awt.Dimension
import javax.swing.JComponent

/**
 * @author yudong
 */
class RefreshLogAction : LogBaseAction("刷新日志", AllIcons.Actions.Refresh) {
    private lateinit var actionButton: ActionButton

    override fun createCustomComponent(presentation: Presentation, place: String): JComponent {
        actionButton = super.createCustomComponent(presentation, place) as MyActionButton

        return actionButton
    }

    fun switchEnabled() {
        var enabled = actionButton.isEnabled

        enabled = !enabled

        actionButton.isEnabled = enabled
    }

    override fun actionPerformed(e: AnActionEvent) {
        val form = getKbsMsgForm(e)

        form.refreshLogData()
    }
}
