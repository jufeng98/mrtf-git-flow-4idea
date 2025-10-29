package com.github.xiaolyuh.action;

import com.github.xiaolyuh.i18n.I18n;
import com.github.xiaolyuh.i18n.I18nKey;
import com.github.xiaolyuh.service.ConfigService;
import com.github.xiaolyuh.service.GitBranchService;
import com.github.xiaolyuh.service.GitFlowPlus;
import com.github.xiaolyuh.service.KubesphereService;
import com.github.xiaolyuh.ui.ServiceDialog;
import com.github.xiaolyuh.utils.ActionUtils;
import com.github.xiaolyuh.utils.NotifyUtil;
import com.github.xiaolyuh.valve.merge.Valve;
import com.github.xiaolyuh.vo.TagOptions;
import com.google.common.collect.Lists;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.ReflectionUtil;
import git4idea.repo.GitRepository;
import org.apache.commons.lang3.exception.ExceptionUtils;
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
    private static final Logger LOG = Logger.getInstance(AbstractMergeAction.class);
    protected GitFlowPlus gitFlowPlus = GitFlowPlus.getInstance();

    public AbstractMergeAction(String txt, String desc, Icon icon) {
        super(txt, desc, icon);
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        if (Objects.isNull(event.getProject())) {
            event.getPresentation().setEnabled(false);
            return;
        }

        ConfigService configService = ConfigService.Companion.getInstance(event.getProject());

        boolean isInit = GitBranchService.isGitProject(event.getProject()) && configService.isInit();
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
        return ActionUpdateThread.EDT;
    }

    /**
     * 设置是否启用和Text
     */
    protected abstract void setEnabledAndText(AnActionEvent event);

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        actionPerformed(event, null);
    }

    void actionPerformed(@NotNull AnActionEvent event, @Nullable TagOptions tagOptions) {
        final Project project = event.getProject();
        @SuppressWarnings("ConstantConditions") final String currentBranch = gitFlowPlus.getCurrentBranch(project);
        final String targetBranch = getTargetBranch(project);

        final boolean isStartTest = getClass() == StartTestAction.class;
        final boolean isStartTestSec = getClass() == StartTestSecAction.class;

        final GitRepository repository = GitBranchService.getCurrentRepository(project);
        if (Objects.isNull(repository)) {
            return;
        }

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

        List<String> finalSelectServices = selectServices;

        new Task.Backgroundable(project, getTaskTitle(project), false) {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                NotifyUtil.notifyGitCommand(event.getProject(), "=========================");
                List<Valve> valves = getValves();
                for (Valve valve : valves) {
                    if (!valve.invoke(project, repository, currentBranch, targetBranch, tagOptions)) {
                        return;
                    }
                }

                // 刷新
                repository.update();

                myProject.getMessageBus().syncPublisher(GitRepository.GIT_REPO_CHANGE).repositoryChanged(repository);

                VirtualFileManager.getInstance().asyncRefresh(null);

                if (isStartTest || isStartTestSec) {
                    finalSelectServices.forEach(serviceName -> {
                        try {
                            KubesphereService kubesphereService = KubesphereService.Companion.getInstance(project);

                            kubesphereService.triggerPipeline(serviceName, isStartTest);
                        } catch (Exception e) {
                            LOG.warn(e);

                            NotifyUtil.notifyError(project, serviceName + "触发流水线出错了:" + ExceptionUtils.getStackTrace(e));
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
