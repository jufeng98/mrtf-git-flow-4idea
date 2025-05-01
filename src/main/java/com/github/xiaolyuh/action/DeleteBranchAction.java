package com.github.xiaolyuh.action;

import com.github.xiaolyuh.i18n.I18n;
import com.github.xiaolyuh.ui.BranchDeleteDialog;
import com.github.xiaolyuh.utils.ConfigUtil;
import com.github.xiaolyuh.utils.GitBranchUtil;
import com.github.xiaolyuh.utils.NotifyUtil;
import com.github.xiaolyuh.valve.merge.ChangeFileValve;
import com.github.xiaolyuh.valve.merge.Valve;
import com.github.xiaolyuh.vo.BranchVo;
import com.github.xiaolyuh.vo.DeleteBranchOptions;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFileManager;
import git4idea.repo.GitRepository;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author yudong
 */
public class DeleteBranchAction extends AbstractMergeAction {

    protected void setEnabledAndText(AnActionEvent event) {
        event.getPresentation().setEnabled(true);
        event.getPresentation().setText(I18n.getContent("DeleteBranchAction.text"));
    }

    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        //noinspection DataFlowIssue
        GitRepository repository = GitBranchUtil.getCurrentRepository(project);
        if (gitFlowPlus.isExistChangeFile(project)) {
            return;
        }

        BranchDeleteDialog branchDeleteDialog = new BranchDeleteDialog(repository);
        if (!branchDeleteDialog.showAndGet()) {
            return;
        }

        int flag = Messages.showOkCancelDialog(project,
                I18n.getContent("DeleteBranchAction.confirm"), I18n.getContent("DeleteBranchAction.text"),
                I18n.getContent("OkText"), I18n.getContent("CancelText"),
                IconLoader.getIcon("/icons/warning.svg", AbstractNewBranchAction.class));
        if (flag != 0) {
            return;
        }

        DeleteBranchOptions deleteBranchOptions = branchDeleteDialog.getDeleteBranchOptions();
        String tip = I18n.getContent("deleting.branches", deleteBranchOptions.getBranches().size());
        new Task.Backgroundable(project, tip, false) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                AtomicInteger i = new AtomicInteger(1);
                double faction = 1.0 / deleteBranchOptions.getBranches().size();

                NotifyUtil.notifyGitCommand(event.getProject(), "=====================================");
                @SuppressWarnings("DataFlowIssue")
                String currentBranchName = repository.getCurrentBranch().getName();

                Map<Boolean, List<BranchVo>> map = deleteBranchOptions.getBranches().stream()
                        .collect(Collectors.groupingBy(it -> it.getBranch().equals(currentBranchName)));

                map.getOrDefault(false, Collections.emptyList())
                        .forEach((branchVo) -> {
                            gitFlowPlus.deleteBranch(repository, branchVo.getBranch(), deleteBranchOptions.isDeleteLocalBranch());
                            indicator.setFraction(faction * (i.getAndIncrement()));
                        });

                List<BranchVo> branchVos = map.get(true);
                if (branchVos != null) {
                    BranchVo branchVo = branchVos.get(0);
                    gitFlowPlus.deleteBranch(repository, "master", branchVo.getBranch());
                    indicator.setFraction(faction * (i.getAndIncrement()));
                }

                repository.update();
                //noinspection DataFlowIssue
                myProject.getMessageBus().syncPublisher(GitRepository.GIT_REPO_CHANGE).repositoryChanged(repository);
                VirtualFileManager.getInstance().asyncRefresh(null);
                String message = I18n.getContent("deleting.branches.finished", deleteBranchOptions.getBranches().size());
                NotifyUtil.notifySuccess(event.getProject(), message);
            }
        }.queue();
    }

    protected String getTargetBranch(Project project) {
        return ConfigUtil.getInitOptions(project).getMasterBranch();
    }

    protected String getDialogTitle(Project project) {
        return I18n.getContent("FailureReleaseAction.DialogTitle");
    }

    protected String getTaskTitle(Project project) {
        String release = ConfigUtil.getInitOptions(project).getReleaseBranch();
        return I18n.getContent("FailureReleaseAction.TaskTitle", release);
    }

    protected List<Valve> getValves() {
        List<Valve> valves = new ArrayList<>();
        valves.add(ChangeFileValve.getInstance());
        return valves;
    }
}
