package com.github.xiaolyuh.http.runconfig

import com.intellij.openapi.options.SettingsEditor
import com.intellij.ui.components.JBTextField
import javax.swing.JComponent
import javax.swing.JPanel

class HttpSettingsEditor : SettingsEditor<HttpRunConfiguration>() {
    override fun resetEditorFrom(s: HttpRunConfiguration) {

    }

    override fun applyEditorTo(s: HttpRunConfiguration) {

    }

    override fun createEditor(): JComponent {
        val jPanel = JPanel()
        jPanel.add(JBTextField("Hello world"))
        return jPanel
    }
}
