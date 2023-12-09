package com.github.xiaolyuh.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.intellij.openapi.project.Project;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ExecutorUtils {
    public static ExecutorService executorService = Executors.newCachedThreadPool();

    public static void addTask(Runnable runnable) {
        executorService.submit(runnable);
    }

    public static void monitorBuildTask(String runsUrl, String id, String selectService, Project project) {
        executorService.submit(() -> {
            sleep(60);
            createMonitorBuildTask(runsUrl, id, selectService, project);
        });
    }

    private static void createMonitorBuildTask(String runsUrl, String id, String selectService, Project project) {
        Runnable task = createMonitorBuildTaskReal(runsUrl, id, selectService, project);
        executorService.submit(task);
    }

    private static Runnable createMonitorBuildTaskReal(String runsUrl, String id, String selectService, Project project) {
        return () -> {
            try {
                JsonObject resObj = HttpClientUtil.getForObjectWithToken(runsUrl + "/" + id + "/", null, JsonObject.class);
                String state = resObj.get("state").getAsString();
                if (!"FINISHED".equals(state)) {
                    sleep(10);
                    // 构建未完成,重新监控
                    createMonitorBuildTask(runsUrl, id, selectService, project);
                    return;
                }
                String result = resObj.get("result").getAsString();
                if (!"SUCCESS".equals(result)) {
                    NotifyUtil.notifyError(project, selectService + " id为" + id + "构建失败");
                    return;
                }
                NotifyUtil.notifySuccess(project, selectService + " id为" + id + "构建成功");

                monitorStartTask(runsUrl, selectService, id, project);

                NotifyUtil.notifyInfo(project, "开始监控" + selectService + " id为" + id + "启动情况");
            } catch (Exception e) {
                NotifyUtil.notifyError(project, "检测" + selectService + " id为" + id + "的构建情况出错,原因:" + e.getMessage());
            }
        };
    }

    public static void monitorStartTask(String runsUrl, String selectService, String id, Project project) {
        executorService.submit(() -> {
            sleep(10);
            String namespace = findNamespace(runsUrl);

            String podUrl = String.format("http://host-kslb.mh.bluemoon.com.cn/kapis/clusters/sim-1/resources.kubesphere.io/v1alpha3/namespaces/%s/pods?name=%s&sortBy=startTime&limit=10",
                    namespace, selectService.toLowerCase());

            String newInstanceName;
            try {
                newInstanceName = findNewInstanceName(podUrl, id, 0);
            } catch (Exception e) {
                NotifyUtil.notifyError(project, "检测" + selectService + " id为" + id +
                        "的启动情况出错啦,原因:" + e.getMessage());
                return;
            }
            NotifyUtil.notifyInfo(project, "新实例" + newInstanceName + "启动中......");

            sleep(10);
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
                JsonObject resObj = HttpClientUtil.getForObjectWithToken(podUrl, null, JsonObject.class);
                JsonArray items = resObj.getAsJsonArray("items");

                List<JsonObject> list = new ArrayList<>();
                for (JsonElement item : items) {
                    JsonObject itemObject = (JsonObject) item;
                    String instanceName = itemObject.getAsJsonObject("metadata").get("name").getAsString();
                    if (instanceName.equals(newInstanceName)) {
                        list.add(itemObject);
                        break;
                    }
                }

                if (list.isEmpty()) {
                    NotifyUtil.notifyError(project,
                            newInstanceName + "实例已不存在,表明已有新实例启动,请自行检查服务情况,目前检测到的实例个数:" + items.size());
                    return;
                }

                JsonObject newItemObject = list.get(0);
                JsonObject statusObj = newItemObject.getAsJsonObject("status");
                int restartCount = getRestartCount(statusObj, "initContainerStatuses");
                if (restartCount > 0) {
                    NotifyUtil.notifyError(project, newInstanceName + "容器初始化失败,当前重启次数:" + restartCount);
                    return;
                }
                restartCount = getRestartCount(statusObj, "containerStatuses");
                if (restartCount > 0) {
                    NotifyUtil.notifyError(project, newInstanceName + "容器启动失败,当前重启次数:" + restartCount);
                    return;
                }

                boolean ready = getReady(statusObj, "initContainerStatuses");
                if (!ready) {
                    sleep(10);
                    monitorStartedTask(podUrl, selectService, project, newInstanceName);
                    return;
                }
                ready = getReady(statusObj, "containerStatuses");
                if (!ready) {
                    sleep(10);
                    monitorStartedTask(podUrl, selectService, project, newInstanceName);
                    return;
                }

                sleep(10);
                NotifyUtil.notifySuccess(project, newInstanceName + "新实例启动成功");
            } catch (Exception e) {
                NotifyUtil.notifyError(project, "检测" + newInstanceName + "启动情况出错,原因:" + e.getMessage());
            }
        };
    }

    private static String findNewInstanceName(String podUrl, String id, int detectTimes) throws Exception {
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
        return findNewInstanceName(podUrl, id, detectTimes);
    }

    private static int getRestartCount(JsonObject statusObj, String key) {
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

    private static boolean getReady(JsonObject statusObj, String key) {
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

    private static String findNamespace(String runsUrl) {
        int start = runsUrl.indexOf("pipelines") + "pipelines".length() + 1;
        int end = runsUrl.indexOf("branches") - 1;
        return runsUrl.substring(start, end);
    }

    public static void sleep(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException ignored) {
        }
    }
}
