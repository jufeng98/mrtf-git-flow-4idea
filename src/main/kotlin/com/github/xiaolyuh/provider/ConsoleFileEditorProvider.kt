package com.github.xiaolyuh.provider

import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorPolicy
import com.intellij.openapi.fileEditor.FileEditorProvider
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

/**
 * @author yudong
 */
class ConsoleFileEditorProvider : FileEditorProvider, DumbAware {

    override fun accept(p0: Project, p1: VirtualFile): Boolean {
        return p1 is ConsoleVirtualFile
    }

    override fun createEditor(p0: Project, p1: VirtualFile): FileEditor {
        return ConsoleFileEditor(p1 as ConsoleVirtualFile)
    }

    override fun getEditorTypeId(): String {
        return "ConsoleFileEditorProvider"
    }

    override fun getPolicy(): FileEditorPolicy {
        return FileEditorPolicy.HIDE_DEFAULT_EDITOR
    }
}
