package com.github.xiaolyuh.action.toolbar

import com.github.xiaolyuh.support.MyActionButton
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.actionSystem.impl.ActionButton
import com.intellij.ui.JBColor
import java.awt.Dimension
import javax.swing.JComponent

/**
 * @author yudong
 */
class SoftWrapAction : LogBaseAction("软换行", AllIcons.Actions.ToggleSoftWrap) {
    private var useSoftWrap = false
    private lateinit var actionButton: ActionButton

    override fun createCustomComponent(presentation: Presentation, place: String): JComponent {
        useSoftWrap = false

        actionButton = super.createCustomComponent(presentation, place) as MyActionButton

        changeActionButton(useSoftWrap)

        return actionButton
    }

    override fun actionPerformed(e: AnActionEvent) {
        useSoftWrap = !useSoftWrap

        changeActionButton(useSoftWrap)

        getEditor(e).settings.isUseSoftWraps = useSoftWrap
    }

    private fun changeActionButton(useSoftWrap: Boolean) {
        if (useSoftWrap) {
            actionButton.background = JBColor.LIGHT_GRAY
        } else {
            actionButton.background = null
        }
    }
}
