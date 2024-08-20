package com.github.xiaolyuh.utils;

import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class VirtualFileUtils {

    public static VirtualFile createlogVirtualFileFromText(byte[] txtBytes) {
        return createVirtualFileFromText(txtBytes, "log");
    }

    @SuppressWarnings("DataFlowIssue")
    public static VirtualFile createVirtualFileFromText(byte[] txtBytes, String suffix) {
        Path tempFile;
        try {
            tempFile = Files.createTempFile("gitFlowPlus-temp-text-", "." + suffix);
            File file = tempFile.toFile();
            VirtualFile virtualFile = LocalFileSystem.getInstance().refreshAndFindFileByPath(file.getAbsolutePath());
            virtualFile.setBinaryContent(txtBytes);
            file.deleteOnExit();
            return virtualFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

