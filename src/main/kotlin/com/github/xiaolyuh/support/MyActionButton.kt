package com.github.xiaolyuh.support

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.actionSystem.ex.ActionUtil.lastUpdateAndCheckDumb
import com.intellij.openapi.actionSystem.impl.ActionButton
import java.awt.Dimension
import java.awt.event.MouseEvent

class MyActionButton(action: AnAction, presentation: Presentation?, place: String, minimumSize: Dimension) :
    ActionButton(action, presentation, place, minimumSize) {
    override fun performAction(e: MouseEvent) {
        val toolbar = ActionToolbar.findToolbarBy(this)
        val kind = if (toolbar is ActionUiKind) {
            toolbar
        } else {
            ActionUiKind.TOOLBAR
        }

        val event = AnActionEvent.createEvent(this.dataContext, this.myPresentation, this.myPlace, kind, e)

        if (lastUpdateAndCheckDumb(this.myAction, event, false) && this.isEnabled) {
            this.actionPerformed(event)

            toolbar?.updateActionsAsync()
        }
    }
}
