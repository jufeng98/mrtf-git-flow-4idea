package com.github.xiaolyuh.http.runconfig

import com.intellij.openapi.options.SettingsEditor
import javax.swing.JComponent
import javax.swing.JPanel

class HttpSettingsEditor : SettingsEditor<HttpRunConfiguration>() {
    override fun resetEditorFrom(s: HttpRunConfiguration) {

    }

    override fun applyEditorTo(s: HttpRunConfiguration) {

    }

    override fun createEditor(): JComponent {
        return JPanel()
    }
}
