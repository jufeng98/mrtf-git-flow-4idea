package com.github.xiaolyuh.action.toolbar

import com.github.xiaolyuh.action.support.MyActionButton
import com.github.xiaolyuh.ui.KbsMsgForm
import com.intellij.execution.ui.ConsoleView
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformCoreDataKeys
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.actionSystem.ex.CustomComponentAction
import java.awt.Dimension
import javax.swing.Icon
import javax.swing.JComponent

/**
 * @author yudong
 */
abstract class LogBaseAction(text: String?, icon: Icon?) : AnAction(text, null, icon), CustomComponentAction {

    fun getConsoleView(e: AnActionEvent): ConsoleView {
        return getKbsMsgForm(e).consoleView
    }

    fun getKbsMsgForm(e: AnActionEvent): KbsMsgForm {
        return PlatformCoreDataKeys.CONTEXT_COMPONENT.getData(e.dataContext) as KbsMsgForm
    }

    override fun createCustomComponent(presentation: Presentation, place: String): JComponent {
        return MyActionButton(this, presentation, place, Dimension(20, 20))
    }
}
