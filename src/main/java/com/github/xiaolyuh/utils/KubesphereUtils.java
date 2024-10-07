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
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class KubesphereUtils {

    public static void triggerPipeline(String selectService, Project project) throws Exception {
        if (StringUtils.isBlank(selectService)) {
            NotifyUtil.notifyInfo(project, "未选择服务,跳过触发流水线");
            return;
        }

        if (ConfigUtil.notExistsK8sOptions(project)) {
            NotifyUtil.notifyInfo(project, "缺少k8s配置文件,跳过触发流水线");
            return;
        }

        String crumbissuerUrl = ConfigUtil.getCrumbissuerUrl(project);
        JsonObject resObj = HttpClientUtil.getForObjectWithToken(crumbissuerUrl, null, JsonObject.class, project);
        String crumb = resObj.get("crumb").getAsString();
        NotifyUtil.notifyInfo(project, "请求url:" + crumbissuerUrl + ",结果crumb:" + crumb);
        if (StringUtils.isBlank(crumb)) {
            throw new RuntimeException(crumbissuerUrl + "配置有误:" + resObj);
        }

        boolean isFrontProject = ConfigUtil.getK8sOptions(project).isFrontProject();
        String reqBody;
        if (isFrontProject) {
            reqBody = "{\"parameters\":[]}";
        } else {
            reqBody = String.format("{\"parameters\":[{\"name\":\"MDL_NAME\",\"value\":\"%s\"}]}", selectService);
        }

        String runsUrl = ConfigUtil.getRunsUrl(project);
        Map<String, String> headers = Maps.newHashMap();
        headers.put("Jenkins-Crumb", crumb);
        resObj = HttpClientUtil.postJsonForObjectWithToken(runsUrl, reqBody, headers, JsonObject.class, project);
        if (!resObj.has("id")) {
            throw new RuntimeException(runsUrl + "配置有误:" + resObj);
        }

        String id = resObj.get("id").getAsString();
        String msg = String.format("请求url:%s,结果id:%s,queueId:%s,state:%s", runsUrl, id,
                resObj.get("queueId").getAsString(), resObj.get("state").getAsString());
        NotifyUtil.notifyInfo(project, msg);

        NotifyUtil.notifyInfo(project, selectService + "触发流水线成功!");

        ExecutorUtils.monitorBuildTask(id, selectService, project);

        NotifyUtil.notifyInfo(project, "开始监控" + selectService + " id为" + id + "的构建情况");
    }

    public static void loginAndSaveToken(Project project) throws Exception {
        kotlin.Pair<String, String> pair = ConfigUtil.getKubesphereUser();
        String kubesphereUsername = pair.getFirst();
        if (StringUtils.isBlank(kubesphereUsername)) {
            throw new RuntimeException("请先在配置菜单配置Kubesphere用户信息");
        }
        String kubespherePassword = pair.getSecond();

        String accessToken = loginByUrl(kubesphereUsername, kubespherePassword, project);

        ConfigUtil.saveKubesphereToken(accessToken, project);
    }

    public static String loginByUrl(String kubesphereUsername, String kubespherePassword, Project project) throws Exception {
        String loginUrl = ConfigUtil.getLoginUrl(project);
        String accessToken;
        if (ConfigUtil.isMhKubesphere(project)) {
            String reqBody = String.format("grant_type=password&username=%s&password=%s", kubesphereUsername, kubespherePassword);
            Map<String, String> headers = Maps.newHashMap();
            headers.put("Content-Type", "application/x-www-form-urlencoded");
            JsonObject jsonObject = HttpClientUtil.postForObject(loginUrl, reqBody, headers, JsonObject.class, project);
            accessToken = jsonObject.get("access_token").getAsString();
        } else {
            JsonObject reqJsonObj = new JsonObject();
            reqJsonObj.addProperty("username", kubesphereUsername);

            String encrypt = encrypt("kubesphere", kubespherePassword);
            reqJsonObj.addProperty("encrypt", encrypt);

            String reqBody = reqJsonObj.toString();
            Map<String, String> reqHeaders = Maps.newHashMap();
            reqHeaders.put("Content-Type", "application/json");
            reqHeaders.put("Accept", "*/*");
            JsonObject jsonObject = HttpClientUtil.postForObject(loginUrl, reqBody, reqHeaders, JsonObject.class, project);
            accessToken = jsonObject.get("access_token").getAsString();
        }

        NotifyUtil.notifyInfo(ProjectUtil.getActiveProject(), "请求url:" + loginUrl + "," + kubesphereUsername + "登录结果accessToken:" + accessToken);
        return accessToken;
    }

    public static boolean isLoginUrl(String url, Project project) {
        return url.equals(ConfigUtil.getLoginUrl(project));
    }

    public static String getTokenFromResponseCookie(List<String> cookies) {
        List<String> list = cookies.stream()
                .filter(it -> it.startsWith("token"))
                .map(it -> it.split(";")[0].split("=")[1])
                .collect(Collectors.toList());
        return list.get(0);
    }

    public static String findInstanceName(String podUrl, String id, int detectTimes, Project project) throws Exception {
        JsonObject resObj = HttpClientUtil.getForObjectWithToken(podUrl, null, JsonObject.class, project);

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

        return findInstanceName(podUrl, id, detectTimes, project);
    }

    public static List<InstanceVo> findInstanceName(Project project, String serviceName) throws Exception {
        String podsUrl = ConfigUtil.getPodsUrl(project, serviceName);
        JsonObject resObj = HttpClientUtil.getForObjectWithToken(podsUrl, null, JsonObject.class, project);

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
                instanceVo = new InstanceVo(instanceName, "未就绪", false);
            }
            instanceVos.add(instanceVo);

        }
        return instanceVos;
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

    public static Pair<byte[], byte[]> getBuildErrorInfo(String url, Project project) {
        String urlPush = url + "/log/?start=0";
        CompletableFuture<byte[]> futurePush = CompletableFuture.supplyAsync(() ->
        {
            try {
                return HttpClientUtil.getForObjectWithToken(urlPush, null, byte[].class, project);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).exceptionally(e -> ExceptionUtils.getStackTrace(e).getBytes(StandardCharsets.UTF_8));

        String compileLogPath = ConfigUtil.getCompileLogPath(project);
        if (StringUtils.isBlank(compileLogPath)) {
            throw new RuntimeException("构建失败了,由于未配置compileLogPath参数,无法获取详细构建错误信息");
        }

        String urlCompile = url + compileLogPath;
        CompletableFuture<byte[]> futureCompile = CompletableFuture.supplyAsync(() ->
        {
            try {
                return HttpClientUtil.getForObjectWithToken(urlCompile, null, byte[].class, project);
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

    public static byte[] getContainerStartInfo(Project project, String selectService, String newInstanceName, int tailLines,
                                               boolean previous, boolean follow) throws Exception {
        String logsUrl = ConfigUtil.getLogsUrl(project, selectService, newInstanceName, tailLines, previous, follow);
        return HttpClientUtil.getForObjectWithTokenUseUrl(logsUrl, null, byte[].class, project);
    }

    public static void getContainerStartInfo(Project project, String selectService, String newInstanceName, int tailLines,
                                             boolean previous, boolean follow, Consumer<byte[]> consumer) throws Exception {
        String logsUrl = ConfigUtil.getLogsUrl(project, selectService, newInstanceName, tailLines, previous, follow);
        HttpClientUtil.getForObjectWithTokenUseUrl(logsUrl, null, byte[].class, consumer, project);
    }

    public static String encrypt(String e, String t) {
        t = Base64.getEncoder().encodeToString(t.getBytes());
        if (t.length() > e.length()) {
            e += t.substring(0, t.length() - e.length());
        }
        StringBuilder binary = new StringBuilder();
        StringBuilder chars = new StringBuilder();
        for (int i = 0; i < e.length(); i++) {
            int k = e.charAt(i);
            int p = i < t.length() ? t.charAt(i) : 64;
            int sum = k + p;
            binary.append(sum % 2 == 0 ? "0" : "1");
            chars.append((char) (sum / 2));
        }
        return Base64.getEncoder().encodeToString(binary.toString().getBytes()) + "@" + chars;
    }

}
