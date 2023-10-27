package com.github.xiaolyuh.test;

import com.alibaba.fastjson.JSONObject;
import com.github.xiaolyuh.test.utils.SftpClient;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class UploadTest {

    @Test
    public void uploadPlugin() throws Exception {
        String name = "git-flow-plus-4idea.zip";
        File file = new File("build/distributions/" + name);
        String configJsonStr = FileUtils.readFileToString(new File("C:\\Users\\Admin\\Documents\\java-config.json"),
                StandardCharsets.UTF_8.name());
        System.out.println("config:" + configJsonStr);
        JSONObject configJson = JSONObject.parseObject(configJsonStr).getJSONObject("ssoInfo");
        SftpClient sftpClient = new SftpClient(configJson.getString("name"), configJson.getString("pwd"),
                configJson.getString("ip"), configJson.getInteger("port"));
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
