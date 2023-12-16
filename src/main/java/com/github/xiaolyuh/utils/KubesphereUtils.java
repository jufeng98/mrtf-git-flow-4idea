package com.github.xiaolyuh.utils;

import com.github.xiaolyuh.vo.InstanceVo;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.intellij.ide.impl.ProjectUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class KubesphereUtils {
    public static final String LOGIN_URL = "http://10.255.243.18:30219/oauth/token";


    public static void triggerPipeline(String selectService, Project project) throws Exception {
        if (StringUtils.isBlank(selectService)) {
            NotifyUtil.notifyInfo(project, "未选择服务,跳过触发流水线");
            return;
        }

        JsonObject configObj = ConfigUtil.getProjectConfigFromFile(project);
        if (configObj == null) {
            NotifyUtil.notifyInfo(project, "缺少配置文件,跳过触发流水线");
            return;
        }

        String crumbissuerUrl = configObj.get("crumbissuerUrl").getAsString();
        JsonObject resObj = HttpClientUtil.getForObjectWithToken(crumbissuerUrl, null, JsonObject.class);
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

    public static void loginAndSaveToken() throws Exception {
        kotlin.Pair<String, String> pair = ConfigUtil.getKubesphereUser();
        String kubesphereUsername = pair.getFirst();
        if (StringUtils.isBlank(kubesphereUsername)) {
            throw new RuntimeException("请先在更新配置菜单配置Kubesphere用户信息");
        }
        String kubespherePassword = pair.getSecond();
        String accessToken = loginByUrl(kubesphereUsername, kubespherePassword);
        ConfigUtil.saveKubesphereToken(accessToken);
    }

    public static String loginByUrl(String kubesphereUsername, String kubespherePassword) throws Exception {
        String reqBody = String.format("grant_type=password&username=%s&password=%s", kubesphereUsername, kubespherePassword);
        Map<String, String> headers = Maps.newHashMap();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        JsonObject jsonObject = HttpClientUtil.postForObject(LOGIN_URL, reqBody, headers, JsonObject.class);
        String accessToken = jsonObject.get("access_token").getAsString();
        NotifyUtil.notifyInfo(ProjectUtil.getActiveProject(), "请求url:" + LOGIN_URL + "," + kubesphereUsername + "登录结果:" + jsonObject);
        return accessToken;
    }

    public static String findNamespace(String runsUrl) {
        int start = runsUrl.indexOf("pipelines") + "pipelines".length() + 1;
        int end = runsUrl.indexOf("branches") - 1;
        return runsUrl.substring(start, end);
    }

    public static String findInstanceName(String podUrl, String id, int detectTimes) throws Exception {
        JsonObject resObj = HttpClientUtil.getForObjectWithToken(podUrl, null, JsonObject.class);

        JsonArray items = resObj.getAsJsonArray("items");
        for (Object item : items) {
            JsonObject itemObject = (JsonObject) item;
            JsonObject specObj = itemObject.getAsJsonObject("spec");
            JsonArray containers = specObj.getAsJsonArray("containers");
            for (Object container : containers) {
                JsonObject containerObject = (JsonObject) container;
                String image = containerObject.get("image").getAsString();
                if (image.endsWith(id)) {
                    return itemObject.getAsJsonObject("metadata").get("name").getAsString();
                }
            }
        }
        detectTimes++;
        if (detectTimes >= 18) {
            throw new RuntimeException("当前url:" + podUrl + ",返回结果:" + resObj);
        }
        TimeUnit.SECONDS.sleep(10);
        return findInstanceName(podUrl, id, detectTimes);
    }

    public static List<InstanceVo> findInstanceName(String runsUrl, String selectService) throws Exception {
        String podUrl = findPodUrl(runsUrl, selectService);

        JsonObject resObj = HttpClientUtil.getForObjectWithToken(podUrl, null, JsonObject.class);

        List<InstanceVo> instanceVos = new ArrayList<>();
        JsonArray items = resObj.getAsJsonArray("items");
        for (Object item : items) {
            JsonObject itemObject = (JsonObject) item;
            JsonObject statusObj = itemObject.getAsJsonObject("status");
            boolean ready1 = getReady(statusObj, "initContainerStatuses");
            boolean ready2 = getReady(statusObj, "containerStatuses");
            String instanceName = itemObject.getAsJsonObject("metadata").get("name").getAsString();
            InstanceVo instanceVo;
            if (ready1 && ready2) {
                instanceVo = new InstanceVo(instanceName, "运行中", false);
            } else {
                instanceVo = new InstanceVo(instanceName, "未就绪", true);
            }
            instanceVos.add(instanceVo);

        }
        return instanceVos;
    }

    public static String findPodUrl(String runsUrl, String selectService) {
        String namespace = findNamespace(runsUrl);

        return String.format("http://host-kslb.mh.bluemoon.com.cn/kapis/clusters/sim-1/resources.kubesphere.io/v1alpha3/namespaces/%s/pods?name=%s&sortBy=startTime&limit=10",
                namespace, selectService.toLowerCase());
    }

    public static int getRestartCount(JsonObject statusObj, String key) {
        JsonArray statuses = statusObj.getAsJsonArray(key);
        if (statuses == null) {
            return 0;
        }
        int sum = 0;
        for (JsonElement status : statuses) {
            JsonObject tmpObj = (JsonObject) status;
            sum += tmpObj.get("restartCount").getAsInt();
        }
        return sum;
    }

    public static boolean getReady(JsonObject statusObj, String key) {
        JsonArray statuses = statusObj.getAsJsonArray(key);
        if (statuses == null) {
            return true;
        }
        List<Boolean> list = new ArrayList<>();
        for (JsonElement status : statuses) {
            JsonObject tmpObj = (JsonObject) status;
            list.add(tmpObj.get("ready").getAsBoolean());

        }
        return list.stream().allMatch(it -> it);
    }

    public static Pair<byte[], byte[]> getBuildErrorInfo(String url) {
        String urlPush = url + "log/?start=0";
        CompletableFuture<byte[]> futurePush = CompletableFuture.supplyAsync(() ->
        {
            try {
                return HttpClientUtil.getForObjectWithToken(urlPush, null, byte[].class);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).exceptionally(e -> ExceptionUtils.getStackTrace(e).getBytes(StandardCharsets.UTF_8));

        String urlCompile = url + "nodes/33/steps/36/log/?start=0";
        CompletableFuture<byte[]> futureCompile = CompletableFuture.supplyAsync(() ->
        {
            try {
                return HttpClientUtil.getForObjectWithToken(urlCompile, null, byte[].class);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).exceptionally(e -> ExceptionUtils.getStackTrace(e).getBytes(StandardCharsets.UTF_8));

        CompletableFuture.allOf(futurePush, futureCompile).join();
        try {
            return Pair.create(futurePush.get(), futureCompile.get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] getContainerStartInfo(String runsUrl, String selectService, String newInstanceName, int tailLines,
                                               boolean previous, boolean follow) throws Exception {
        String namespace = findNamespace(runsUrl);
        String logUrl = String.format("http://host-kslb.mh.bluemoon.com.cn/api/clusters/sim-1/v1/namespaces/%s/pods/%s/log?container=%s&tailLines=%s&timestamps=true&follow=%s&previous=%s",
                namespace, newInstanceName, selectService.toLowerCase(), tailLines, follow, previous);
        return HttpClientUtil.getForObjectWithTokenUseUrl(logUrl, null, byte[].class);
    }

    public static void getContainerStartInfo(String runsUrl, String selectService, String newInstanceName, int tailLines,
                                             boolean previous, boolean follow, Consumer<byte[]> consumer) throws Exception {
        String namespace = findNamespace(runsUrl);
        String logUrl = String.format("http://host-kslb.mh.bluemoon.com.cn/api/clusters/sim-1/v1/namespaces/%s/pods/%s/log?container=%s&tailLines=%s&timestamps=true&follow=%s&previous=%s",
                namespace, newInstanceName, selectService.toLowerCase(), tailLines, follow, previous);
        HttpClientUtil.getForObjectWithTokenUseUrl(logUrl, null, byte[].class, consumer);
    }

}
