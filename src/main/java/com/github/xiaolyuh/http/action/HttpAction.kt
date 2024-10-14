package com.github.xiaolyuh.http.action

import com.github.xiaolyuh.http.gutter.HttpGutterIconClickHandler
import com.github.xiaolyuh.http.psi.HttpMethod
import com.github.xiaolyuh.http.ui.HttpEditorTopForm
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.ex.EditorGutterComponentEx

class HttpAction(private val httpMethod: HttpMethod) : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val component = e.inputEvent.component as EditorGutterComponentEx
        val navigationHandler = HttpGutterIconClickHandler(httpMethod)

        val selectedEnv = HttpEditorTopForm.getCurrentEditorSelectedEnv(httpMethod.project)
        navigationHandler.doRequest(component, selectedEnv)
    }

}
