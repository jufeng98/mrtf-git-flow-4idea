package com.github.xiaolyuh.provider

import com.github.xiaolyuh.ui.JcefK8sConsoleForm
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorState
import com.intellij.openapi.util.UserDataHolderBase
import com.intellij.openapi.vfs.VirtualFile
import java.beans.PropertyChangeListener
import javax.swing.JComponent

/**
 * @author yudong
 */
class ConsoleFileEditor(private val consoleVirtualFile: ConsoleVirtualFile) : FileEditor, UserDataHolderBase() {
    val form: JcefK8sConsoleForm

    init {
        val instanceVo = consoleVirtualFile.instanceVo
        val project = consoleVirtualFile.project
        val selectService = consoleVirtualFile.selectService
        val mainTest = consoleVirtualFile.mainTest

        form = JcefK8sConsoleForm(instanceVo, project, selectService, mainTest)
    }

    override fun getFile(): VirtualFile {
        return consoleVirtualFile
    }

    override fun dispose() {
        form.dispose()
    }

    override fun getComponent(): JComponent {
        return form.mainPanel
    }

    override fun getPreferredFocusedComponent(): JComponent {
        return form.mainPanel
    }

    override fun getName(): String {
        return "ConsoleFileEditor"
    }

    override fun setState(p0: FileEditorState) {

    }

    override fun isModified(): Boolean {
        return false
    }

    override fun isValid(): Boolean {
        return true
    }

    override fun addPropertyChangeListener(p0: PropertyChangeListener) {
    }

    override fun removePropertyChangeListener(p0: PropertyChangeListener) {
    }

}
