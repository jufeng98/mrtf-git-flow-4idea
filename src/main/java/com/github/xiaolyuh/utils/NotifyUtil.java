package com.github.xiaolyuh.utils;

import com.github.xiaolyuh.service.ConfigService;
import com.google.gson.JsonParser;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.jsoup.internal.StringUtil;

import java.util.Date;


/**
 * 通知弹框
 *
 * @author yuhao.wang3
 */
public class NotifyUtil {
    private static final NotificationGroup STICKY_BALLOON_GROUP = NotificationGroupManager.getInstance()
            .getNotificationGroup("GitflowPlus.STICKY_BALLOON");

    private static final NotificationGroup NONE = NotificationGroupManager.getInstance()
            .getNotificationGroup("GitflowPlus.NONE");

    public static void notifySuccess(Project project, String title, String message) {
        notify(NotificationType.INFORMATION, STICKY_BALLOON_GROUP, project, title, message);
    }

    public static void notifySuccess(Project project, String message) {
        notify(NotificationType.INFORMATION, STICKY_BALLOON_GROUP, project, "温馨提示", message);
    }

    public static void notifyError(Project project, String message) {
        notify(NotificationType.ERROR, STICKY_BALLOON_GROUP, project, "温馨提示", message);
    }

    public static void notifyError(Project project, String title, String message) {
        notify(NotificationType.ERROR, STICKY_BALLOON_GROUP, project, title, message);
    }

    public static void notifyInfo(Project project, String msg) {
        notify(NotificationType.INFORMATION, NONE, project, "温馨提示", msg);
    }

    public static void notifyGitCommand(Project project, String command) {
        notify(NotificationType.INFORMATION, NONE, project, "Git command", command);
    }

    private static void notify(NotificationType type, NotificationGroup group, Project project,
                               String title, String message) {
        if (project == null) {
            return;
        }

        notifyWebhook(project, title, message);

        group.createNotification(title, message, type).notify(project);
    }

    // 内容模板 当前时间 - 执行任务(标题) - 执行结果(内容) - 所属项目
    private static final String contentTemplate = "%s - %s - %s - %s";

    // 新增webhook
    public static void notifyWebhook(Project project, String title, String message) {
        try {
            ConfigService configService = ConfigService.Companion.getInstance(project);
            String url = configService.getInitOptions().getFsWebHookUrl();
            if (StringUtil.isBlank(url)) {
                return;
            }

            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            StackTraceElement traceElement = stackTrace[4];
            if (!traceElement.getClassName().equals(KubesphereUtils.class.getName())
                    && !traceElement.getClassName().equals(ExecutorUtils.class.getName())) {
                return;
            }

            String now = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
            String content = String.format(contentTemplate, now, title, message, project.getName());
            HttpClientUtil.postApplicationJson(url, generateJSONStr(content), String.class, project);
        } catch (Exception ignored) {
        }
    }

    public static Object generateJSONStr(String Content) {
        String template = "{\"msg_type\":\"text\",\"content\":{\"text\":\"%s\"}}";
        return JsonParser.parseString(String.format(template, Content));
    }
}
