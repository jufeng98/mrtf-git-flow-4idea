package com.github.xiaolyuh.utils;

import com.github.xiaolyuh.service.RunTask;
import com.github.xiaolyuh.ui.KbsMsgDialog;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ExecutorUtils {
    private static final Logger LOG = Logger.getInstance(ExecutorUtils.class);
    public static ExecutorService executorService = Executors.newCachedThreadPool();

    public static Future<?> addTask(RunTask runnable) {
        return executorService.submit(runnable);
    }

    public static void monitorBuildTask(String id, String selectService, Project project) {
        addTask(() -> {
            sleep(30);

            createMonitorBuildTask(id, selectService, project);
        });
    }

    private static void createMonitorBuildTask(String id, String selectService, Project project) {
        RunTask task = createMonitorBuildTaskReal(id, selectService, project);

        addTask(task);
    }

    private static RunTask createMonitorBuildTaskReal(String id, String selectService, Project project) {
        return () -> {
            try {
                String url = ConfigUtil.getRunsUrl(project) + "/" + id;
                JsonObject resObj = HttpClientUtil.getForObjectWithToken(url + "/", null, JsonObject.class, project);
                String state = resObj.get("state").getAsString();
                if (!"FINISHED".equals(state)) {
                    sleep(10);

                    // 构建未完成,重新监控
                    createMonitorBuildTask(id, selectService, project);
                    return;
                }

                String result = resObj.get("result").getAsString();
                if (!"SUCCESS".equals(result)) {
                    String title = selectService + " id为" + id + "构建失败";
                    NotifyUtil.notifyInfo(project, title);

                    Pair<byte[], byte[]> pair = KubesphereUtils.getBuildErrorInfo(url, project);

                    ApplicationManager.getApplication().invokeLater(() -> {
                        KbsMsgDialog dialog = new KbsMsgDialog(title, pair, project);
                        dialog.show();
                    }, ModalityState.NON_MODAL);

                    return;
                }

                NotifyUtil.notifySuccess(project, selectService + " id为" + id + "构建成功");

                monitorStartTask(selectService, id, project);

                NotifyUtil.notifyInfo(project, "开始监控" + selectService + " id为" + id + "启动情况");
            } catch (Exception e) {
                LOG.warn(e);
                NotifyUtil.notifyError(project, "检测" + selectService + " id为" + id + "的构建情况出错,原因:" + ExceptionUtils.getStackTrace(e));
            }
        };
    }

    public static void monitorStartTask(String selectService, String id, Project project) {
        addTask(() -> {
            sleep(10);

            String podUrl = ConfigUtil.getPodsUrl(project, selectService);

            String newInstanceName;
            try {
                newInstanceName = KubesphereUtils.findInstanceName(podUrl, id, 0, project);
            } catch (Exception e) {
                NotifyUtil.notifyError(project, "检测" + selectService + " id为" + id +
                        "的启动情况出错啦,原因:" + ExceptionUtils.getStackTrace(e));
                return;
            }

            NotifyUtil.notifyInfo(project, "新实例" + newInstanceName + "启动中......");

            sleep(10);

            monitorStartedTask(podUrl, selectService, project, newInstanceName);
        });
    }

    public static void monitorStartedTask(String podUrl, String selectService, Project project,
                                          String newInstanceName) {
        RunTask task = createMonitorStartTask(podUrl, selectService, project, newInstanceName);

        addTask(task);
    }

    public static RunTask createMonitorStartTask(String podUrl, String selectService, Project project,
                                                 String newInstanceName) {
        return () -> {
            try {
                JsonObject resObj = HttpClientUtil.getForObjectWithToken(podUrl, null, JsonObject.class, project);
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
                    NotifyUtil.notifyError(project, newInstanceName + "实例已不存在,直接开始监控实例数量,当前数量:" + items.size());

                    monitorServiceNumTask(podUrl, selectService, project);

                    return;
                }

                JsonObject newItemObject = list.get(0);

                checkNewInstance(newItemObject, newInstanceName, project, selectService, podUrl);
            } catch (Exception e) {
                NotifyUtil.notifyError(project, "检测" + newInstanceName + "启动情况出错,原因:" + ExceptionUtils.getStackTrace(e));
            }
        };
    }

    public static void checkNewInstance(JsonObject newItemObject, String newInstanceName, Project project,
                                         String selectService, String podUrl) throws Exception {
        JsonObject statusObj = newItemObject.getAsJsonObject("status");
        int restartCount = KubesphereUtils.getRestartCount(statusObj, "initContainerStatuses");
        if (restartCount > 0) {
            sleep(30);

            String title = newInstanceName + "容器初始化失败,当前重启次数:" + restartCount;
            NotifyUtil.notifyInfo(project, title);

            byte[] errorBytes = KubesphereUtils.getContainerStartInfo(project, selectService, newInstanceName,
                    500, false, false);

            ApplicationManager.getApplication().invokeLater(() -> {
                KbsMsgDialog dialog = new KbsMsgDialog(title, errorBytes, project, selectService,
                        newInstanceName, false);
                dialog.show();
            }, ModalityState.NON_MODAL);

            return;
        }

        restartCount = KubesphereUtils.getRestartCount(statusObj, "containerStatuses");
        if (restartCount > 0) {
            sleep(30);

            String title = newInstanceName + "容器启动失败,当前重启次数:" + restartCount;
            NotifyUtil.notifyInfo(project, title);

            byte[] errorBytes = KubesphereUtils.getContainerStartInfo(project, selectService, newInstanceName,
                    500, false, false);

            ApplicationManager.getApplication().invokeLater(() -> {
                KbsMsgDialog dialog = new KbsMsgDialog(title, errorBytes, project, selectService,
                        newInstanceName, false);
                dialog.show();
            }, ModalityState.NON_MODAL);

            return;
        }

        boolean ready = KubesphereUtils.getReady(statusObj, "initContainerStatuses");
        if (!ready) {
            sleep(10);

            monitorStartedTask(podUrl, selectService, project, newInstanceName);

            return;
        }

        ready = KubesphereUtils.getReady(statusObj, "containerStatuses");
        if (!ready) {
            sleep(10);

            monitorStartedTask(podUrl, selectService, project, newInstanceName);

            return;
        }

        NotifyUtil.notifySuccess(project, newInstanceName + "新实例已准备就绪,开始监控实例数量");

        monitorServiceNumTask(podUrl, selectService, project);
    }

    public static void monitorServiceNumTask(String podUrl, String selectService, Project project) {
        RunTask task = createMonitorServiceNumTask(podUrl, selectService, project);

        addTask(task);
    }

    public static RunTask createMonitorServiceNumTask(String podUrl, String selectService, Project project) {
        return () -> {
            try {
                JsonObject resObj = HttpClientUtil.getForObjectWithToken(podUrl, null, JsonObject.class, project);
                JsonArray items = resObj.getAsJsonArray("items");
                if (items.size() > 1) {
                    sleep(10);

                    monitorServiceNumTask(podUrl, selectService, project);

                    return;
                }

                NotifyUtil.notifySuccess(project, selectService + "新实例已完全替换成功!");
            } catch (Exception e) {
                NotifyUtil.notifyError(project, "检测" + selectService + "服务实例数量出错,原因:" + ExceptionUtils.getStackTrace(e));
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
