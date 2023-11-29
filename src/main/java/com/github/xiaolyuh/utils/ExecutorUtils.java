package com.github.xiaolyuh.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.intellij.openapi.project.Project;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.github.xiaolyuh.utils.ConfigUtil.PREFERENCES;

public class ExecutorUtils {
    public static ExecutorService executorService = Executors.newCachedThreadPool();

    public static void monitorBuildTask(String runsUrl, String id, String selectService, Project project) {
        executorService.submit(() -> {
            try {
                TimeUnit.SECONDS.sleep(30);
            } catch (InterruptedException ignored) {
            }
            Runnable task = createMonitorBuildTask(runsUrl, id, selectService, project);
            executorService.submit(task);
        });
    }

    private static Runnable createMonitorBuildTask(String runsUrl, String id, String selectService, Project project) {
        return () -> {
            try {
                String kubesphereToken = PREFERENCES.get("kubesphereToken", "");
                Map<String, String> headers = Maps.newHashMap();
                headers.put("Cookie", "token=" + kubesphereToken);
                JSONObject resObj = OkHttpClientUtil.get(runsUrl + "/" + id + "/", headers, JSONObject.class);
                String state = resObj.getString("state");
                if (!"FINISHED".equals(state)) {
                    TimeUnit.SECONDS.sleep(10);
                    // 构建未完成,重新监控
                    monitorBuildTask(runsUrl, id, selectService, project);
                    return;
                }
                String result = resObj.getString("result");
                NotifyUtil.notifyInfo(project, selectService + "构建结果:" + result);
            } catch (Exception e) {
                NotifyUtil.notifyWarn(project, "检测" + selectService + "构建情况出错,原因:" + e.getMessage());
            }
        };
    }

    public static void monitorStartTask(String runsUrl, String selectService, Project project) {
        executorService.submit(() -> {
            try {
                TimeUnit.SECONDS.sleep(60);
            } catch (InterruptedException ignored) {
            }
            String namespace = findNamespace(runsUrl);

            String podUrl = String.format("http://host-kslb.mh.bluemoon.com.cn/kapis/clusters/sim-1/resources.kubesphere.io/v1alpha3/namespaces/%s/pods?name=%s&sortBy=startTime&limit=10",
                    namespace, selectService);

            String newInstanceName;
            try {
                newInstanceName = findNewInstanceName(podUrl);
            } catch (Exception e) {
                NotifyUtil.notifyWarn(project, "检测" + selectService + "启动情况出错啦,原因:" + e.getMessage());
                return;
            }
            NotifyUtil.notifyWarn(project, "新实例" + newInstanceName + "启动中...");
            monitorStartedTask(podUrl, selectService, project, newInstanceName);
        });
    }

    public static void monitorStartedTask(String podUrl, String selectService, Project project, String newInstanceName) {
        Runnable task = createMonitorStartTask(podUrl, selectService, project, newInstanceName);
        executorService.submit(task);
    }

    public static Runnable createMonitorStartTask(String podUrl, String selectService, Project project, String newInstanceName) {
        return () -> {
            try {
                String kubesphereToken = PREFERENCES.get("kubesphereToken", "");
                Map<String, String> headers = Maps.newHashMap();
                headers.put("Cookie", "token=" + kubesphereToken);
                JSONObject resObj = OkHttpClientUtil.get(podUrl, headers, JSONObject.class);

                JSONArray items = resObj.getJSONArray("items");
                JSONObject newItemObject = (JSONObject) items.stream()
                        .filter(it -> {
                            JSONObject itemObject = (JSONObject) it;
                            String instanceName = itemObject.getJSONObject("metadata").getString("name");
                            return instanceName.equals(newInstanceName);
                        })
                        .collect(Collectors.toList())
                        .get(0);

                JSONObject statusObj = newItemObject.getJSONObject("status");
                int restartCount = getRestartCount(statusObj, "initContainerStatuses");
                if (restartCount > 0) {
                    NotifyUtil.notifyError(project, selectService + "容器初始化失败,当前重启次数:" + restartCount);
                    return;
                }
                restartCount = getRestartCount(statusObj, "containerStatuses");
                if (restartCount > 0) {
                    NotifyUtil.notifyError(project, selectService + "容器启动失败,当前重启次数:" + restartCount);
                    return;
                }

                boolean ready = getReady(statusObj, "initContainerStatuses");
                if (!ready) {
                    TimeUnit.SECONDS.sleep(10);
                    monitorStartedTask(podUrl, selectService, project, newInstanceName);
                    return;
                }
                ready = getReady(statusObj, "containerStatuses");
                if (!ready) {
                    TimeUnit.SECONDS.sleep(10);
                    monitorStartedTask(podUrl, selectService, project, newInstanceName);
                    return;
                }

                NotifyUtil.notifyInfo(project, newInstanceName + "新实例启动成功");
            } catch (Exception e) {
                NotifyUtil.notifyWarn(project, "检测" + selectService + "启动情况出错,原因:" + e.getMessage());
            }
        };
    }

    private static String findNewInstanceName(String podUrl) throws Exception {
        String kubesphereToken = PREFERENCES.get("kubesphereToken", "");
        Map<String, String> headers = Maps.newHashMap();
        headers.put("Cookie", "token=" + kubesphereToken);
        JSONObject resObj = OkHttpClientUtil.get(podUrl, headers, JSONObject.class);

        int totalItems = resObj.getIntValue("totalItems");
        if (totalItems <= 1) {
            TimeUnit.SECONDS.sleep(10);
            return findNewInstanceName(podUrl);
        }

        JSONArray items = resObj.getJSONArray("items");
        JSONObject itemObject = (JSONObject) items.get(0);
        return itemObject.getJSONObject("metadata").getString("name");
    }

    private static int getRestartCount(JSONObject statusObj, String key) {
        JSONArray statuses = statusObj.getJSONArray(key);
        JSONObject tmpObj = (JSONObject) statuses.get(0);
        return tmpObj.getIntValue("restartCount");
    }

    private static boolean getReady(JSONObject statusObj, String key) {
        JSONArray statuses = statusObj.getJSONArray(key);
        JSONObject tmpObj = (JSONObject) statuses.get(0);
        return tmpObj.getBooleanValue("ready");
    }

    private static String findNamespace(String runsUrl) {
        int start = runsUrl.indexOf("pipelines") + "pipelines".length() + 1;
        int end = runsUrl.indexOf("branches") - 1;
        return runsUrl.substring(start, end);
    }
}
