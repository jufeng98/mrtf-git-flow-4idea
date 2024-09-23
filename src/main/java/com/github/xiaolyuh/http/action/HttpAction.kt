package com.github.xiaolyuh.http.action

import com.github.xiaolyuh.http.gutter.HttpGutterIconClickHandler
import com.github.xiaolyuh.http.psi.HttpMethod
import com.github.xiaolyuh.http.ui.HttpEditorTopForm
import com.intellij.execution.lineMarker.LineMarkerActionWrapper.LOCATION_WRAPPER
import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.ex.EditorGutterComponentEx

class HttpAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        @Suppress("INACCESSIBLE_TYPE")
        val dataContext = DataManager.getInstance().loadFromDataContext(e.dataContext, LOCATION_WRAPPER)

        @Suppress("INACCESSIBLE_TYPE", "INACCESSIBLE_TYPE")
        val element = dataContext!!.first as HttpMethod

        val component = e.inputEvent.component as EditorGutterComponentEx
        val navigationHandler = HttpGutterIconClickHandler(element)

        val selectedEnv = HttpEditorTopForm.getSelectedEnv(element.project)
        navigationHandler.doRequest(component, selectedEnv)
    }

}
