package com.github.xiaolyuh.action;

import com.github.xiaolyuh.GitFlowPlus;
import com.github.xiaolyuh.i18n.I18n;
import com.github.xiaolyuh.i18n.I18nKey;
import com.github.xiaolyuh.ui.MergeRequestDialog;
import com.github.xiaolyuh.utils.CollectionUtils;
import com.github.xiaolyuh.utils.ConfigUtil;
import com.github.xiaolyuh.utils.GitBranchUtil;
import com.github.xiaolyuh.utils.NotifyUtil;
import com.github.xiaolyuh.valve.merge.Valve;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.ReflectionUtil;
import git4idea.commands.GitCommandResult;
import git4idea.repo.GitRepository;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * merge request
 *
 * @author yuhao.wang3
 */
public class MergeRequestAction extends AbstractMergeAction {
    protected GitFlowPlus gitFlowPlus = GitFlowPlus.getInstance();

    public MergeRequestAction() {
        super("Merge Request", "发起 code review", IconLoader.getIcon("/icons/mergeToTest.svg", Objects.requireNonNull(ReflectionUtil.getGrandCallerClass())));
    }

    @Override
    protected void setEnabledAndText(AnActionEvent event) {
        event.getPresentation().setText(I18n.getContent(I18nKey.MERGE_REQUEST_ACTION$TEXT));
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        @SuppressWarnings("ConstantConditions")
        final String currentBranch = gitFlowPlus.getCurrentBranch(project);
        final String targetBranch = ConfigUtil.getInitOptions(project).getTestBranch();
        final GitRepository repository = GitBranchUtil.getCurrentRepository(project);
        if (Objects.isNull(repository)) {
            return;
        }

        GitCommandResult result = gitFlowPlus.getLocalLastCommit(repository, currentBranch);
        String[] msgs = result.getOutputAsJoinedString().split("-body:");
        MergeRequestDialog mergeRequestDialog = new MergeRequestDialog(project, msgs.length >= 1 ? msgs[0] : "", msgs.length >= 2 ? msgs[1] : "");
        mergeRequestDialog.show();
        if (!mergeRequestDialog.isOK()) {
            return;
        }

        new Task.Backgroundable(project, "Merge request", false) {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                NotifyUtil.notifyGitCommand(event.getProject(), "==================================");

                String tempBranchName = currentBranch + "_temp";
                // 删除分支
                gitFlowPlus.deleteBranch(repository, currentBranch, tempBranchName);
                // 新建分支
                GitCommandResult result = gitFlowPlus.newNewBranchByLocalBranch(repository, currentBranch, tempBranchName);
                if (!result.success()) {
                    NotifyUtil.notifyError(project, "Error", result.getErrorOutputAsJoinedString());
                    return;
                }
                // 发起merge request
                result = gitFlowPlus.mergeRequest(repository, tempBranchName, targetBranch, mergeRequestDialog.getMergeRequestOptions());
                if (!result.success()) {
                    NotifyUtil.notifyError(project, "Error", result.getErrorOutputAsJoinedString());
                }
                NotifyUtil.notifySuccess(project, "Success", result.getErrorOutputAsHtmlString());
                if (CollectionUtils.isNotEmpty(result.getErrorOutput()) && result.getErrorOutput().size() > 3) {
                    String address = result.getErrorOutput().get(2);
                    address = address.split(" {3}")[1];
                    BrowserUtil.browse(address);
                }
                // 删除本地临时分支
                result = gitFlowPlus.deleteLocalBranch(repository, currentBranch, tempBranchName);
                if (!result.success()) {
                    NotifyUtil.notifyError(project, "Error", result.getErrorOutputAsJoinedString());
                }
                // 刷新
                repository.update();
                myProject.getMessageBus().syncPublisher(GitRepository.GIT_REPO_CHANGE).repositoryChanged(repository);
                VirtualFileManager.getInstance().asyncRefresh(null);
            }
        }.queue();
    }

    @Override
    protected String getTargetBranch(Project project) {
        return null;
    }

    @Override
    protected String getDialogTitle(Project project) {
        return null;
    }

    @Override
    protected String getTaskTitle(Project project) {
        return null;
    }

    @Override
    protected List<Valve> getValves() {
        return null;
    }


}



