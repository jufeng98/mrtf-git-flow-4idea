package com.github.xiaolyuh.utils

import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.vfs.VfsUtil.findFileByIoFile
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.readText
import java.io.File
import java.nio.file.Files

/**
 * @author yudong
 */
object VirtualFileUtils {

    fun readNewestContent(file: File): String? {
        val virtualFile = findFileByIoFile(file, true)

        if (virtualFile != null && isVirtualFileNewest(virtualFile, file)) {
            return readNewestContent(virtualFile)
        }

        if (!file.exists()) {
            return null
        }

        return Files.readString(file.toPath())
    }

    fun readNewestContent(virtualFile: VirtualFile): String {
        val document = FileDocumentManager.getInstance().getCachedDocument(virtualFile)

        return document?.text ?: virtualFile.readText()
    }

    private fun isVirtualFileNewest(virtualFile: VirtualFile, file: File): Boolean {
        if (!file.exists()) {
            return false
        }

        val timeStamp = virtualFile.timeStamp
        val millis = Files.getLastModifiedTime(file.toPath()).toMillis()
        return timeStamp >= millis
    }
}

