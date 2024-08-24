package com.github.xiaolyuh.http.editor

import com.github.xiaolyuh.http.HttpFileType
import com.github.xiaolyuh.http.service.EnvFileService
import com.github.xiaolyuh.http.ui.HttpEditorTopForm
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.vfs.VirtualFile

class HttpEditorListener : FileEditorManagerListener {
    override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
        if (file.fileType !is HttpFileType) {
            return
        }

        val fileEditor = source.getSelectedEditor(file)!!
        val envFileService = EnvFileService.getService(source.project)

        val httpEditorTopForm = HttpEditorTopForm()
        httpEditorTopForm.initData(envFileService)

        fileEditor.putUserData(HttpEditorTopForm.KEY, httpEditorTopForm)

        source.addTopComponent(fileEditor, httpEditorTopForm.mainPanel)
    }
}
