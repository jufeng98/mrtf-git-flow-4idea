package com.github.xiaolyuh.utils;

import com.github.xiaolyuh.ui.KbsMsgDialog;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.github.xiaolyuh.utils.KubesphereUtils.findInstanceName;
import static com.github.xiaolyuh.utils.KubesphereUtils.findPodUrl;
import static com.github.xiaolyuh.utils.KubesphereUtils.getReady;
import static com.github.xiaolyuh.utils.KubesphereUtils.getRestartCount;

public class ExecutorUtils {
    public static ExecutorService executorService = Executors.newCachedThreadPool();

    public static void addTask(RunTask runnable) {
        executorService.submit(runnable);
    }

    public static void monitorBuildTask(String runsUrl, String id, String selectService, Project project) {
        addTask(() -> {
            sleep(30);
            createMonitorBuildTask(runsUrl, id, selectService, project);
        });
    }

    private static void createMonitorBuildTask(String runsUrl, String id, String selectService, Project project) {
        RunTask task = createMonitorBuildTaskReal(runsUrl, id, selectService, project);
        addTask(task);
    }

    private static RunTask createMonitorBuildTaskReal(String runsUrl, String id, String selectService, Project project) {
        return () -> {
            try {
                String url = runsUrl + "/" + id + "/";
                JsonObject resObj = HttpClientUtil.getForObjectWithToken(url, null, JsonObject.class);
                String state = resObj.get("state").getAsString();
                if (!"FINISHED".equals(state)) {
                    sleep(10);
                    // 构建未完成,重新监控
                    createMonitorBuildTask(runsUrl, id, selectService, project);
                    return;
                }
                String result = resObj.get("result").getAsString();
                if (!"SUCCESS".equals(result)) {
                    String title = selectService + " id为" + id + "构建失败";
                    NotifyUtil.notifyInfo(project, title);
                    Pair<String, String> pair = KubesphereUtils.getBuildErrorInfo(url);
                    ApplicationManager.getApplication().invokeLater(() -> {
                        KbsMsgDialog dialog = new KbsMsgDialog(title, pair, project);
                        dialog.show();
                    }, ModalityState.NON_MODAL);
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
        addTask(() -> {
            sleep(10);

            String podUrl = findPodUrl(runsUrl, selectService);

            String newInstanceName;
            try {
                newInstanceName = findInstanceName(podUrl, id, 0);
            } catch (Exception e) {
                NotifyUtil.notifyError(project, "检测" + selectService + " id为" + id +
                        "的启动情况出错啦,原因:" + e.getMessage());
                return;
            }
            NotifyUtil.notifyInfo(project, "新实例" + newInstanceName + "启动中......");

            sleep(10);
            monitorStartedTask(podUrl, selectService, runsUrl, project, newInstanceName);
        });
    }

    public static void monitorStartedTask(String podUrl, String selectService, String runsUrl, Project project,
                                          String newInstanceName) {
        RunTask task = createMonitorStartTask(podUrl, selectService, runsUrl, project, newInstanceName);
        addTask(task);
    }

    public static RunTask createMonitorStartTask(String podUrl, String selectService, String runsUrl, Project project,
                                                 String newInstanceName) {
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
                    sleep(30);
                    String title = newInstanceName + "容器初始化失败,当前重启次数:" + restartCount;
                    NotifyUtil.notifyInfo(project, title);
                    String error = KubesphereUtils.getContainerStartInfo(runsUrl, selectService, newInstanceName,
                            300, false, false);
                    ApplicationManager.getApplication().invokeLater(() -> {
                        KbsMsgDialog dialog = new KbsMsgDialog(title, error, project, selectService, runsUrl,
                                newInstanceName, false);
                        dialog.show();
                    }, ModalityState.NON_MODAL);
                    return;
                }
                restartCount = getRestartCount(statusObj, "containerStatuses");
                if (restartCount > 0) {
                    sleep(30);
                    String title = newInstanceName + "容器启动失败,当前重启次数:" + restartCount;
                    NotifyUtil.notifyInfo(project, title);
                    String error = KubesphereUtils.getContainerStartInfo(runsUrl, selectService, newInstanceName,
                            300, false, false);
                    ApplicationManager.getApplication().invokeLater(() -> {
                        KbsMsgDialog dialog = new KbsMsgDialog(title, error, project, selectService, runsUrl,
                                newInstanceName, false);
                        dialog.show();
                    }, ModalityState.NON_MODAL);
                    return;
                }

                boolean ready = getReady(statusObj, "initContainerStatuses");
                if (!ready) {
                    sleep(10);
                    monitorStartedTask(podUrl, selectService, runsUrl, project, newInstanceName);
                    return;
                }
                ready = getReady(statusObj, "containerStatuses");
                if (!ready) {
                    sleep(10);
                    monitorStartedTask(podUrl, selectService, runsUrl, project, newInstanceName);
                    return;
                }

                sleep(10);
                NotifyUtil.notifySuccess(project, newInstanceName + "新实例启动成功");
            } catch (Exception e) {
                NotifyUtil.notifyError(project, "检测" + newInstanceName + "启动情况出错,原因:" + e.getMessage());
            }
        };
    }

    public static void sleep(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException ignored) {
        }
    }
}
