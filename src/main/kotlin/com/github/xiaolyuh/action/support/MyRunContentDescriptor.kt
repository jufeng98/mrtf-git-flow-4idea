package com.github.xiaolyuh.action.support

import com.github.xiaolyuh.ui.KbsMsgForm
import com.intellij.execution.ui.RunContentDescriptor

/**
 * @author yudong
 */
class MyRunContentDescriptor(private val form: KbsMsgForm, displayName: String, activationCallback: Runnable) :
    RunContentDescriptor(null, null, form.mainPanel, displayName, null, activationCallback) {

    override fun dispose() {
        super.dispose()
        form.dispose()
    }

}
