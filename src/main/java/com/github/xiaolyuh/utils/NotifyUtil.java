package com.github.xiaolyuh.utils;

import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;

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
        group.createNotification(title, message, type).notify(project);
    }
}
