package com.github.xiaolyuh.utils;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.LightVirtualFile;

import java.io.IOException;

public class VirtualFileUtils {

    public static VirtualFile createLogVirtualFileFromText(byte[] txtBytes) {
        return createVirtualFileFromText(txtBytes);
    }

    private static VirtualFile createVirtualFileFromText(byte[] txtBytes) {
        try {
            LightVirtualFile lightVirtualFile = new LightVirtualFile("temp-gitFlowPlus.log");
            lightVirtualFile.setBinaryContent(txtBytes);
            return lightVirtualFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

