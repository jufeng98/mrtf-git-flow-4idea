package com.github.xiaolyuh.utils;

import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class VirtualFileUtils {

    @SuppressWarnings("DataFlowIssue")
    public static VirtualFile createVirtualFileFromText(String content) {
        Path tempFile;
        try {
            tempFile = Files.createTempFile("k8s-", ".log");
            File file = tempFile.toFile();
            VirtualFile virtualFile = LocalFileSystem.getInstance()
                    .refreshAndFindFileByPath(file.getAbsolutePath());
            virtualFile.setBinaryContent(content.getBytes());
            file.deleteOnExit();
            return virtualFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}

