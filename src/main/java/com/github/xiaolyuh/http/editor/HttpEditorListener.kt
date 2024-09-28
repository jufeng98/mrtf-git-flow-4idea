package com.github.xiaolyuh.http.editor

import com.github.xiaolyuh.http.HttpFileType
import com.github.xiaolyuh.http.ui.HttpEditorTopForm
import com.github.xiaolyuh.utils.NotifyUtil
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.vfs.VirtualFile

class HttpEditorListener : FileEditorManagerListener {
    override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
        if (file.fileType !is HttpFileType) {
            return
        }

        val fileEditor = source.getSelectedEditor(file) ?: return
        val project = source.project

        val module = ModuleUtil.findModuleForFile(file, project) ?: return

        val httpEditorTopForm = HttpEditorTopForm()

        try {
            httpEditorTopForm.initEnvCombo(module, file.parent.path)
        } catch (e: Exception) {
            NotifyUtil.notifyError(project, e.message)
        }

        fileEditor.putUserData(HttpEditorTopForm.KEY, httpEditorTopForm)

        source.addTopComponent(fileEditor, httpEditorTopForm.mainPanel)
    }

}
