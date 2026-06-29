package com.github.xiaolyuh.action;

import com.github.xiaolyuh.i18n.I18n;
import com.github.xiaolyuh.i18n.I18nKey;
import com.github.xiaolyuh.service.*;
import com.github.xiaolyuh.utils.NotifyUtil;
import com.github.xiaolyuh.utils.StringUtils;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFileManager;
import git4idea.commands.GitCommandResult;
import git4idea.repo.GitRepository;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Objects;

/**
 * 新建分支
 *
 * @author yuhao.wang3
 */
public abstract class AbstractNewBranchAction extends AnAction {
    GitFlowPlus gitFlowPlus = GitFlowPlus.getInstance();

    public AbstractNewBranchAction(String text, String desc, Icon icon) {
        super(text, desc, icon);
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        if (project == null) {
            return;
        }

        boolean enabled = GitBranchService.isGitProject(project) && ConfigService.Companion.getInstance(project).isInit();

        event.getPresentation().setEnabled(enabled);

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
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getProject();
        if (project == null) {
            return;
        }

        String featurePrefix = getPrefix(project);

        // 获取输入框内容
        String inputString = getInputString(project);
        if (StringUtils.isBlank(inputString)) {
            return;
        }

        // 获取开发分支完整名称
        String newBranchName = featurePrefix + inputString;

        GitRepository repository = GitBranchService.getCurrentRepository(project);
        if (Objects.isNull(repository)) {
            return;
        }

        if (gitFlowPlus.isExistChangeFile(project)) {
            return;
        }

        new Task.Backgroundable(project, getTitle(newBranchName), false) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                String master = ConfigService.Companion.getInstance(project).getInitOptions().getMasterBranch();

                NotifyUtil.notifyGitCommand(event.getProject(), "==================================");

                if (isDeleteBranch()) {
                    // 删除分支
                    GitCommandResult result = gitFlowPlus.deleteBranch(repository, master, newBranchName);
                    if (result.success()) {
                        NotifyUtil.notifySuccess(myProject, "Success", I18n.getContent(I18nKey.DELETE_BRANCH_SUCCESS, newBranchName));
                    } else {
                        NotifyUtil.notifyError(myProject, "Error",
                                I18n.getContent(I18nKey.DELETE_BRANCH_ERROR) + "：" + result.getErrorOutputAsJoinedString());
                    }
                }

                // 新建分支
                GitCommandResult result = gitFlowPlus.newNewBranchBaseRemoteMaster(repository, master, newBranchName);
                if (result.success()) {
                    NotifyUtil.notifySuccess(myProject, "Success",
                            I18n.getContent(I18nKey.NEW_BRANCH_SUCCESS, master, newBranchName));
                } else {
                    NotifyUtil.notifyError(myProject, "Error", result.getErrorOutputAsJoinedString());
                }

                // 刷新
                repository.update();

                VirtualFileManager.getInstance().asyncRefresh(null);
            }
        }.queue();

    }

    /**
     * 获取分支前缀
     *
     * @param project Project
     * @return String
     */
    abstract public String getPrefix(Project project);

    /**
     * 获取输入的分支名称
     *
     * @param project Project
     * @return String
     */
    abstract public String getInputString(Project project);

    /**
     * 获取标题
     *
     * @param branchName branchName
     * @return String
     */
    abstract public String getTitle(String branchName);

    /**
     * 是否先删除分支
     *
     * @return boolean
     */
    public boolean isDeleteBranch() {
        return true;
    }

}
