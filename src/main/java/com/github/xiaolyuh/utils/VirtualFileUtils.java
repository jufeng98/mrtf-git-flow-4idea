package com.github.xiaolyuh.utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.lang.RandomStringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class VirtualFileUtils {

    public static VirtualFile createLogVirtualFileFromText(byte[] txtBytes) {
        return createVirtualFileFromText(txtBytes, "log");
    }

    @SuppressWarnings("DataFlowIssue")
    public static VirtualFile createVirtualFileFromText(byte[] txtBytes, String suffix) {
        Path tempFile;
        try {
            tempFile = Files.createTempFile("temp-gitFlowPlus-", "." + suffix);
            File file = tempFile.toFile();
            VirtualFile virtualFile = LocalFileSystem.getInstance().refreshAndFindFileByPath(file.getAbsolutePath());
            virtualFile.setBinaryContent(txtBytes);
            file.deleteOnExit();
            return virtualFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings({"DataFlowIssue", "ResultOfMethodCallIgnored"})
    public static VirtualFile createHttpVirtualFileFromText(byte[] txtBytes, String suffix, Project project) {
        Path tempFile;
        try {
            File parentDir = new File(project.getBasePath() + "/.idea", "httpClient");
            if (!parentDir.exists()) {
                parentDir.mkdir();
            }

            Path path = Path.of(parentDir.toString(), "http-" + RandomStringUtils.randomNumeric(6) + "." + suffix);
            tempFile = Files.createFile(path);
            File file = tempFile.toFile();
            VirtualFile virtualFile = LocalFileSystem.getInstance().refreshAndFindFileByPath(file.getAbsolutePath());
            virtualFile.setBinaryContent(txtBytes);
            return virtualFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

