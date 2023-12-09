package com.github.xiaolyuh.test;

import com.github.xiaolyuh.test.utils.SftpClient;
import com.github.xiaolyuh.utils.HttpClientUtil;
import com.google.gson.JsonObject;
import org.apache.commons.io.FileUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@Ignore
public class UploadTest {

    @Test
    public void uploadPlugin() throws Exception {
        File file = new File("build/distributions/git-flow-plus-4idea-1.2.1.zip");
        String configJsonStr = FileUtils.readFileToString(new File("C:\\Users\\Admin\\Documents\\java-config.json"),
                StandardCharsets.UTF_8.name());
        System.out.println("config:" + configJsonStr);
        JsonObject configJson = HttpClientUtil.gson.fromJson(configJsonStr, JsonObject.class).getAsJsonObject("ssoInfo");
        SftpClient sftpClient = new SftpClient(configJson.get("name").getAsString(), configJson.get("pwd").getAsString(),
                configJson.get("ip").getAsString(), configJson.get("port").getAsInt());
        String name = "git-flow-plus-4idea.zip";
        try {
            sftpClient.connect();
            System.out.println("开始上传");
            sftpClient.upload("/data/static/FE/activity", "lodop", name, Files.newInputStream(file.toPath()));
            System.out.println("上传成功");
        } finally {
            sftpClient.disconnect();
        }
    }

}
