package com.github.xiaolyuh.utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

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
    public static VirtualFile createHttpVirtualFileFromText(byte[] txtBytes, String suffix, Project project, String tabName) {
        Date date = new Date();
        Path tempFile;
        try {
            String dayStr = DateFormatUtils.format(date, "MM-dd");
            File parentDir = new File(project.getBasePath() + "/.idea/httpClient", dayStr);
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }

            String str = StringUtils.defaultString(tabName) + "-" + DateFormatUtils.format(date, "hhmmss");
            Path path = Path.of(parentDir.toString(), "tmp-" + str + "." + suffix);
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

