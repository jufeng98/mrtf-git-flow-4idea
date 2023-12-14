package com.github.xiaolyuh.utils;

import com.github.xiaolyuh.net.HttpException;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.io.StreamUtil;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class KubesphereUtils {

    public static void triggerPipeline(String selectService, Project project) {
        if (StringUtils.isBlank(selectService)) {
            NotifyUtil.notifyInfo(project, "未选择服务,跳过触发流水线");
            return;
        }

        JsonObject configObj = ConfigUtil.getProjectConfigToFile(project);
        if (configObj == null) {
            NotifyUtil.notifyInfo(project, "缺少配置文件,跳过触发流水线");
            return;
        }

        String kubesphereToken = ConfigUtil.getKubesphereToken();
        if (StringUtils.isBlank(kubesphereToken)) {
            loginAndSaveToken(project);
        }

        kubesphereToken = ConfigUtil.getKubesphereToken();
        if (StringUtils.isBlank(kubesphereToken)) {
            NotifyUtil.notifyError(project, "请先配置Kubesphere用户信息");
            return;
        }

        String crumbissuerUrl = configObj.get("crumbissuerUrl").getAsString();
        JsonObject resObj;
        try {
            resObj = HttpClientUtil.getForObjectWithToken(crumbissuerUrl, null, JsonObject.class);
        } catch (HttpException e) {
            if (e.getCode() == 401) {
                NotifyUtil.notifyInfo(project, "token失效,尝试重新登录");
                loginAndSaveToken(project);
                resObj = HttpClientUtil.getForObjectWithToken(crumbissuerUrl, null, JsonObject.class);
            } else {
                throw e;
            }
        }
        String crumb = resObj.get("crumb").getAsString();
        NotifyUtil.notifyInfo(project, "请求url:" + crumbissuerUrl + ",结果crumb:" + crumb);

        boolean isFrontProject = configObj.get("isFrontProject").getAsBoolean();
        String reqBody;
        if (isFrontProject) {
            reqBody = "{\"parameters\":[]}";
        } else {
            reqBody = String.format("{\"parameters\":[{\"name\":\"MDL_NAME\",\"value\":\"%s\"}]}", selectService);
        }

        String runsUrl = configObj.get("runsUrl").getAsString();
        Map<String, String> headers = new HashMap<>();
        headers.put("Jenkins-Crumb", crumb);
        resObj = HttpClientUtil.postJsonForObjectWithToken(runsUrl, reqBody, headers, JsonObject.class);

        String id = resObj.get("id").getAsString();
        String msg = String.format("请求url:%s,结果id:%s,queueId:%s,state:%s", runsUrl, id,
                resObj.get("queueId").getAsString(), resObj.get("state").getAsString());
        NotifyUtil.notifyInfo(project, msg);

        NotifyUtil.notifyInfo(project, selectService + "触发流水线成功!");

        ExecutorUtils.monitorBuildTask(runsUrl, id, selectService, project);

        NotifyUtil.notifyInfo(project, "开始监控" + selectService + " id为" + id + "的构建情况");
    }

    public static void loginAndSaveToken(Project project) {
        String kubesphereUsername = ConfigUtil.getKubesphereToken();
        if (StringUtils.isBlank(kubesphereUsername)) {
            NotifyUtil.notifyError(project, "温馨提示", "未配置Kubesphere用户信息");
            return;
        }
        String kubespherePassword = ConfigUtil.getKubesphereToken();
        String accessToken = loginByUrl(kubesphereUsername, kubespherePassword, project);
        ConfigUtil.saveKubesphereToken(accessToken);
    }

    public static String loginByUrl(String kubesphereUsername, String kubespherePassword, Project project) {
        try {
            String reqBody = String.format("grant_type=password&username=%s&password=%s", kubesphereUsername, kubespherePassword);
            Map<String, String> headers = Maps.newHashMap();
            headers.put("Content-Type", "application/x-www-form-urlencoded");
            String url = "http://10.255.243.18:30219/oauth/token";
            JsonObject jsonObject = HttpClientUtil.postForObject(url, reqBody, headers, JsonObject.class);
            String accessToken = jsonObject.get("access_token").getAsString();
            NotifyUtil.notifyInfo(project, "请求url:" + url + ",登录结果:" + jsonObject);
            return accessToken;
        } catch (Exception e) {
            throw new RuntimeException(String.format("登录失败,用户名:%s,密码:%s", kubesphereUsername, kubespherePassword), e);
        }
    }

    public static Pair<String, String> getBuildErrorInfo(String url) {
        String urlPush = url + "nodes/33/steps/36/log/?start=0";
        String urlCompile = url + "log/?start=0";
        CompletableFuture<String> futureCompile = CompletableFuture.supplyAsync(() ->
                HttpClientUtil.getForObjectWithToken(urlCompile, null, String.class));
        CompletableFuture<String> futurePush = CompletableFuture.supplyAsync(() ->
                HttpClientUtil.getForObjectWithToken(urlPush, null, String.class));
        CompletableFuture.allOf(futureCompile, futurePush).join();
        try {
            String res1 = futureCompile.get();
            String res2 = futurePush.get();
            return Pair.create(res1, res2);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings({"unused", "ExtractMethodRecommender"})
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
                    ConfigUtil.saveKubesphereToken(token);
                    NotifyUtil.notifyInfo(project, "成功登录Kubesphere,Token:" + token);
                    break;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
