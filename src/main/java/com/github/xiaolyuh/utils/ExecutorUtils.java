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
                if (!"SUCCESS".equals(result)) {
                    NotifyUtil.notifyError(project, selectService + " id为" + id + "构建失败");
                    return;
                }
                NotifyUtil.notifyInfo(project, selectService + " id为" + id + "构建成功");

                monitorStartTask(runsUrl, selectService, id, project);

                NotifyUtil.notifyWarn(project, "开始监控" + selectService + " id为" + id + "启动情况");
            } catch (Exception e) {
                NotifyUtil.notifyWarn(project, "检测" + selectService + " id为" + id + "的构建情况出错,原因:" + e.getMessage());
            }
        };
    }

    public static void monitorStartTask(String runsUrl, String selectService, String id, Project project) {
        executorService.submit(() -> {
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException ignored) {
            }
            String namespace = findNamespace(runsUrl);

            String podUrl = String.format("http://host-kslb.mh.bluemoon.com.cn/kapis/clusters/sim-1/resources.kubesphere.io/v1alpha3/namespaces/%s/pods?name=%s&sortBy=startTime&limit=10",
                    namespace, selectService.toLowerCase());

            String newInstanceName;
            try {
                newInstanceName = findNewInstanceName(podUrl, id, 0);
            } catch (Exception e) {
                NotifyUtil.notifyWarn(project, "检测" + selectService + " id为" + id +
                        "的启动情况出错啦,原因:" + e.getMessage());
                return;
            }
            NotifyUtil.notifyWarn(project, "新实例" + newInstanceName + "启动中......");
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

                TimeUnit.SECONDS.sleep(15);
                NotifyUtil.notifyInfo(project, newInstanceName + "新实例启动成功");
            } catch (Exception e) {
                NotifyUtil.notifyWarn(project, "检测" + selectService + "启动情况出错,原因:" + e.getMessage());
            }
        };
    }

    private static String findNewInstanceName(String podUrl, String id, int detectTimes) throws Exception {
        String kubesphereToken = PREFERENCES.get("kubesphereToken", "");
        Map<String, String> headers = Maps.newHashMap();
        headers.put("Cookie", "token=" + kubesphereToken);
        JSONObject resObj = OkHttpClientUtil.get(podUrl, headers, JSONObject.class);

        JSONArray items = resObj.getJSONArray("items");
        for (Object item : items) {
            JSONObject itemObject = (JSONObject) item;
            JSONObject specObj = itemObject.getJSONObject("spec");
            JSONArray containers = specObj.getJSONArray("containers");
            for (Object container : containers) {
                JSONObject containerObject = (JSONObject) container;
                String image = containerObject.getString("image");
                if (image.endsWith(id)) {
                    return itemObject.getJSONObject("metadata").getString("name");
                }
            }
        }
        detectTimes++;
        if (detectTimes >= 18) {
            throw new RuntimeException("当前url:" + podUrl + ",返回结果:" + resObj.toJSONString());
        }
        TimeUnit.SECONDS.sleep(10);
        return findNewInstanceName(podUrl, id, detectTimes);
    }

    private static int getRestartCount(JSONObject statusObj, String key) {
        JSONArray statuses = statusObj.getJSONArray(key);
        if (statuses == null) {
            return 0;
        }
        return statuses.stream()
                .mapToInt(it -> {
                    JSONObject tmpObj = (JSONObject) it;
                    return tmpObj.getIntValue("restartCount");
                })
                .sum();
    }

    private static boolean getReady(JSONObject statusObj, String key) {
        JSONArray statuses = statusObj.getJSONArray(key);
        if (statuses == null) {
            return true;
        }
        return statuses.stream()
                .allMatch(it -> {
                    JSONObject tmpObj = (JSONObject) it;
                    return tmpObj.getBooleanValue("ready");
                });
    }

    private static String findNamespace(String runsUrl) {
        int start = runsUrl.indexOf("pipelines") + "pipelines".length() + 1;
        int end = runsUrl.indexOf("branches") - 1;
        return runsUrl.substring(start, end);
    }
}
