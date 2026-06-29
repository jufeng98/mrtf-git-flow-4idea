package com.github.xiaolyuh.action;

import com.github.xiaolyuh.i18n.I18n;
import com.github.xiaolyuh.i18n.I18nKey;
import com.github.xiaolyuh.logger.GitFlowPlusLogger;
import com.github.xiaolyuh.service.*;
import com.github.xiaolyuh.ui.ServiceDialog;
import com.github.xiaolyuh.utils.*;
import com.github.xiaolyuh.valve.merge.Valve;
import com.github.xiaolyuh.vo.TagOptions;
import com.google.common.collect.Lists;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.ReflectionUtil;
import git4idea.repo.GitRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;
import java.util.Objects;


/**
 * Merge 抽象Action
 *
 * @author yuhao.wang3
 */
public abstract class AbstractMergeAction extends AnAction {
    protected GitFlowPlus gitFlowPlus = GitFlowPlus.getInstance();

    public AbstractMergeAction(String txt, String desc, Icon icon) {
        super(txt, desc, icon);
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        if (Objects.isNull(project)) {
            event.getPresentation().setEnabled(false);
            return;
        }

        boolean isInit = GitBranchService.isGitProject(project) && ConfigService.Companion.getInstance(project).isInit();
        if (!isInit) {
            event.getPresentation().setEnabled(false);
            return;
        }

        // 已经初始化并且前缀是开发分支才可用
        boolean isDevBranch = ActionUtils.INSTANCE.isDevBranch(event);

        event.getPresentation().setEnabled(isDevBranch);

        setEnabledAndText(event);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    /**
     * 设置是否启用和Text
     */
    protected abstract void setEnabledAndText(AnActionEvent event);

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        if (project == null) {
            return;
        }

        actionPerformed(project, null);
    }

    void actionPerformed(@NotNull Project project, @Nullable TagOptions tagOptions) {
        GitRepository repository = GitBranchService.getCurrentRepository(project);
        if (Objects.isNull(repository)) {
            return;
        }

        String currentBranch = gitFlowPlus.getCurrentBranch(project);
        if (currentBranch == null) {
            return;
        }

        boolean isStartTest = getClass() == StartTestAction.class;
        boolean isStartTestSec = getClass() == StartTestSecAction.class;

        boolean clickOk;
        List<String> selectServices = Lists.newArrayList();
        if (isStartTest || isStartTestSec) {
            ServiceDialog serviceDialog = new ServiceDialog(getDialogContent(project, true), project);
            serviceDialog.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            serviceDialog.show();

            clickOk = serviceDialog.isOK();

            selectServices = serviceDialog.getSelectServices();
        } else {
            int flag = Messages.showOkCancelDialog(project, getDialogContent(project, false),
                    getDialogTitle(project), I18n.getContent(I18nKey.OK_TEXT), I18n.getContent(I18nKey.CANCEL_TEXT),
                    IconLoader.getIcon("/icons/warning.svg", Objects.requireNonNull(ReflectionUtil.getGrandCallerClass())));

            clickOk = flag == 0;
        }

        if (!clickOk) {
            return;
        }

        String targetBranch = getTargetBranch(project);

        List<String> finalSelectServices = selectServices;

        new Task.Backgroundable(project, getTaskTitle(project), false) {

            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                NotifyUtil.notifyGitCommand(project, "=========================");
                List<Valve> valves = getValves();
                for (Valve valve : valves) {
                    if (!valve.invoke(project, repository, currentBranch, targetBranch, tagOptions)) {
                        return;
                    }
                }

                // 刷新
                repository.update();

                project.getMessageBus().syncPublisher(GitRepository.GIT_REPO_CHANGE).repositoryChanged(repository);

                VirtualFileManager.getInstance().asyncRefresh(null);

                if (isStartTest || isStartTestSec) {
                    finalSelectServices.forEach(serviceName -> {
                        try {
                            KubesphereService kubesphereService = KubesphereService.Companion.getInstance(project);

                            kubesphereService.triggerPipeline(serviceName, isStartTest);
                        } catch (Exception e) {
                            GitFlowPlusLogger.INSTANCE.logWarn("触发流水线出错了", e);

                            NotifyUtil.notifyError(project, serviceName + "触发流水线出错了,堆栈信息:" + ExceptionUtils.getStackTrace(e));
                        }
                    });
                }
            }
        }.queue();
    }

    /**
     * 获取目标分支
     *
     * @param project project
     * @return String
     */
    protected abstract String getTargetBranch(Project project);

    /**
     * 获取标题
     *
     * @param project project
     * @return String
     */
    protected abstract String getDialogTitle(Project project);

    /**
     * 获取弹框内容
     *
     * @param project project
     * @return String
     */
    protected String getDialogContent(Project project, boolean isStartTest) {
        String other = isStartTest ? I18n.getContent(I18nKey.MERGE_BRANCH_MSG_OTHER) : "";
        return I18n.getContent(I18nKey.MERGE_BRANCH_MSG, gitFlowPlus.getCurrentBranch(project),
                getTargetBranch(project), other);
    }

    /**
     * 获取Task标题
     *
     * @param project project
     * @return String
     */
    protected abstract String getTaskTitle(Project project);

    /**
     * 获取需要执行的阀门
     *
     * @return boolean
     */
    protected abstract List<Valve> getValves();
}
