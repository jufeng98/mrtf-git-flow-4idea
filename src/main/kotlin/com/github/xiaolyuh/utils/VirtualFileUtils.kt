package com.github.xiaolyuh.utils

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.testFramework.LightVirtualFile

/**
 * @author yudong
 */
object VirtualFileUtils {

    fun createLogVirtualFileFromText(txtBytes: ByteArray): VirtualFile {
        val lightVirtualFile = LightVirtualFile("temp-gitFlowPlus.log")
        lightVirtualFile.setBinaryContent(txtBytes)
        return lightVirtualFile
    }

}

