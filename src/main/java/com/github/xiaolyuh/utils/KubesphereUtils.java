package com.github.xiaolyuh.utils;

import com.alibaba.fastjson.JSONObject;
import com.github.xiaolyuh.HttpException;
import com.google.common.collect.Maps;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.StreamUtil;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static com.github.xiaolyuh.utils.ConfigUtil.PREFERENCES;

public class KubesphereUtils {

    public static void triggerPipeline(String selectService, Project project) {
        if (StringUtils.isBlank(selectService)) {
            NotifyUtil.notifyInfo(project, "未选择服务,跳过触发流水线");
            return;
        }

        JSONObject configObj = ConfigUtil.getProjectConfigToFile(project);
        if (configObj == null) {
            NotifyUtil.notifyInfo(project, "缺少配置文件,跳过触发流水线");
            return;
        }

        String kubesphereToken = PREFERENCES.get("kubesphereToken", "");
        if (StringUtils.isBlank(kubesphereToken)) {
            loginAndSaveToken(project);
        }

        kubesphereToken = PREFERENCES.get("kubesphereToken", "");
        if (StringUtils.isBlank(kubesphereToken)) {
            NotifyUtil.notifyError(project, "请先配置Kubesphere用户信息");
            return;
        }

        String crumbissuerUrl = configObj.getString("crumbissuerUrl");
        String runsUrl = configObj.getString("runsUrl");
        Boolean isFrontProject = configObj.getBoolean("isFrontProject");

        Map<String, String> headers = Maps.newHashMap();
        headers.put("Cookie", "token=" + kubesphereToken);
        JSONObject resObj;
        try {
            resObj = OkHttpClientUtil.get(crumbissuerUrl, headers, JSONObject.class);
        } catch (HttpException e) {
            if (e.getCode() == 401) {
                NotifyUtil.notifyInfo(project, "token失效,尝试重新登录");
                loginAndSaveToken(project);
                kubesphereToken = PREFERENCES.get("kubesphereToken", "");
                headers.put("Cookie", "token=" + kubesphereToken);
                resObj = OkHttpClientUtil.get(crumbissuerUrl, headers, JSONObject.class);
            } else {
                throw e;
            }
        }
        String crumb = resObj.getString("crumb");
        NotifyUtil.notifyInfo(project, "请求url:" + crumbissuerUrl + ",结果crumb:" + crumb);

        headers.put("Jenkins-Crumb", crumb);

        RequestBody requestBody;
        if (isFrontProject) {
            requestBody = FormBody.create(MediaType.parse("application/json"), "{\"parameters\":[]}");
        } else {
            requestBody = FormBody.create(MediaType.parse("application/json")
                    , String.format("{\"parameters\":[{\"name\":\"MDL_NAME\",\"value\":\"%s\"}]}", selectService));
        }
        resObj = OkHttpClientUtil.post(runsUrl, "runsUrl", requestBody, null, headers, JSONObject.class);

        String id = resObj.getString("id");
        String msg = String.format("请求url:%s,结果id:%s,queueId:%s,state:%s", runsUrl, id, resObj.getString("queueId"),
                resObj.getString("state"));
        NotifyUtil.notifyInfo(project, msg);

        NotifyUtil.notifyInfo(project, selectService + "触发流水线成功!");

        ExecutorUtils.monitorBuildTask(runsUrl, id, selectService, project);

        NotifyUtil.notifyInfo(project, "开始监控" + selectService + " id为" + id + "的构建情况");
    }

    public static void loginAndSaveToken(Project project) {
        String kubesphereUsername = PREFERENCES.get("kubesphereUsername", "");
        if (StringUtils.isBlank(kubesphereUsername)) {
            NotifyUtil.notifyError(project, "温馨提示", "未配置Kubesphere用户信息");
            return;
        }
        String kubespherePassword = PREFERENCES.get("kubespherePassword", "");
        String accessToken = loginByUrl(kubesphereUsername, kubespherePassword, project);
        PREFERENCES.put("kubesphereToken", accessToken);
    }

    public static String loginByUrl(String kubesphereUsername, String kubespherePassword, Project project) {
        try {
            RequestBody requestBody = FormBody.create(MediaType.parse("application/x-www-form-urlencoded")
                    , String.format("grant_type=password&username=%s&password=%s", kubesphereUsername, kubespherePassword));
            String url = "http://10.255.243.18:30219/oauth/token";
            JSONObject jsonObject = OkHttpClientUtil.post(url, "登录接口", requestBody, null, null, JSONObject.class);
            String accessToken = jsonObject.getString("access_token");
            NotifyUtil.notifyInfo(project, "请求url:" + url + ",登录结果:" + jsonObject.toJSONString());
            return accessToken;
        } catch (Exception e) {
            throw new RuntimeException(String.format("登录失败,用户名:%s,密码:%s", kubesphereUsername, kubespherePassword), e);
        }
    }

    @SuppressWarnings("unused")
    public static void loginByWsl(String kubesphereUsername, String kubespherePassword, Project project) {
        try {
            String[] cmd;
            String command = "wsl curl \"http://host-kslb.mh.bluemoon.com.cn/login\" -H \"Content-Type:application/json\" -H \"Accept:*/*\" -H \"Connection: close\" -d '{\"username\":\"%s\",\"encrypt\":\"%s\"}' -i";
            command = String.format(command, kubesphereUsername, kubespherePassword);
            cmd = new String[]{"cmd", "/C", command};
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(cmd);
            process.waitFor();
            @SuppressWarnings("deprecation")
            String res = StreamUtil.readText(process.getInputStream(), StandardCharsets.UTF_8);
            String[] split = res.split("\r\n");
            for (String line : split) {
                if (line.startsWith("Set-Cookie: token=")) {
                    String token = line.replace("Set-Cookie: token=", "").split(";")[0];
                    PREFERENCES.put("kubesphereToken", token);
                    NotifyUtil.notifyInfo(project, "成功登录Kubesphere,Token:" + token);
                    break;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
