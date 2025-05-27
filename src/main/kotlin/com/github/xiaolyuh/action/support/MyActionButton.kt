package com.github.xiaolyuh.action.support

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.actionSystem.ex.ActionUtil.lastUpdateAndCheckDumb
import com.intellij.openapi.actionSystem.impl.ActionButton
import java.awt.Dimension
import java.awt.event.MouseEvent

/**
 * @author yudong
 */
class MyActionButton(action: AnAction, presentation: Presentation?, place: String, minimumSize: Dimension) :
    ActionButton(action, presentation, place, minimumSize) {

    @Suppress("DEPRECATION")
    override fun performAction(e: MouseEvent) {
        super.performAction(e)

        @Suppress("removal")
        val event = AnActionEvent(e, dataContext, myPlace, presentation, ActionManager.getInstance(), e.modifiers)

        if (lastUpdateAndCheckDumb(this.myAction, event, false) && this.isEnabled) {
            this.actionPerformed(event)
        }
    }
}
