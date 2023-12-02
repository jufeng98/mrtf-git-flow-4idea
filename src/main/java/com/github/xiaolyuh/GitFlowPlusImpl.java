package com.github.xiaolyuh;

import com.github.xiaolyuh.i18n.I18n;
import com.github.xiaolyuh.i18n.I18nKey;
import com.github.xiaolyuh.utils.*;
import com.google.common.collect.Lists;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ChangeListManager;
import com.intellij.vcsUtil.VcsUtil;
import git4idea.GitUtil;
import git4idea.commands.GitCommandResult;
import git4idea.commands.GitSimpleEventDetector;
import git4idea.i18n.GitBundle;
import git4idea.merge.GitMergeCommittingConflictResolver;
import git4idea.merge.GitMerger;
import git4idea.repo.GitRepository;
import git4idea.util.GitFileUtils;
import git4idea.util.GitUIUtil;
import git4idea.util.StringScanner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author yuhao.wang3
 * @since 2020/3/23 9:53
 */
public class GitFlowPlusImpl implements GitFlowPlus {
    private static boolean mockLockFlag = false;

    private final Git git = Git.getInstance();

    @Override
    public void addConfigToGit(GitRepository repository) {
        try {
            String filePath = repository.getProject().getBasePath() + File.separator + Constants.CONFIG_FILE_NAME;
            FilePath path = VcsUtil.getFilePath(filePath);
            GitFileUtils.addPaths(repository.getProject(), repository.getRoot(), Lists.newArrayList(path));
        } catch (VcsException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public GitCommandResult newNewBranchBaseRemoteMaster(@NotNull GitRepository repository, @Nullable String master, @NotNull String newBranchName) {
        git.fetchNewBranchByRemoteMaster(repository, master, newBranchName);
        git.checkout(repository, newBranchName);

        // 推送分支
        return git.push(repository, newBranchName, true);
    }

    @Override
    public GitCommandResult newNewBranchByLocalBranch(@NotNull GitRepository repository, @Nullable String localBranchName, @NotNull String newBranchName) {
        git.checkout(repository, localBranchName);
        git.branch(repository, newBranchName);
        return git.checkout(repository, newBranchName);
    }

    @Override
    public GitCommandResult deleteBranch(@NotNull GitRepository repository,
                                         @Nullable String checkoutBranchName,
                                         @Nullable String branchName) {

        git.checkout(repository, checkoutBranchName);
        git.deleteRemoteBranch(repository, branchName);
        return git.deleteLocalBranch(repository, branchName);
    }


    @Override
    public GitCommandResult deleteLocalBranch(@NotNull GitRepository repository,
                                              @Nullable String checkoutBranchName,
                                              @Nullable String branchName) {

        git.checkout(repository, checkoutBranchName);
        return git.deleteLocalBranch(repository, branchName);
    }

    @Override
    public String getCurrentBranch(@NotNull Project project) {
        GitRepository repository = GitBranchUtil.getCurrentRepository(project);
        return repository.getCurrentBranch().getName();
    }

    @Override
    public String getRemoteLastCommit(@NotNull GitRepository repository, @Nullable String remoteBranchName) {
        git.fetch(repository);
        GitCommandResult result = git.showRemoteLastCommit(repository, remoteBranchName);
        GitCommandResult lastReleaseTimeResult = git.getLastReleaseTime(repository);
        String msg = result.getOutputAsJoinedString();
        msg = msg.replaceFirst("Author:", "\r\n  Author: ");
        msg = msg.replaceFirst("-Message:", ";\r\n  Message: ");

        String lastReleaseTime = lastReleaseTimeResult.getOutputAsJoinedString();
        if (StringUtils.isNotBlank(lastReleaseTime)) {
            lastReleaseTime = lastReleaseTime.substring(lastReleaseTime.indexOf("@{") + 2, lastReleaseTime.indexOf(" +"));
            msg = msg + "\r\n  Date: " + lastReleaseTime;
        }

        return msg;
    }

    @Override
    public GitCommandResult getLocalLastCommit(@NotNull GitRepository repository, @Nullable String branchName) {
        git.fetch(repository);
        return git.showLocalLastCommit(repository, branchName);
    }

    @Override
    public GitCommandResult mergeBranchAndPush(GitRepository repository, String currentBranch, String targetBranch,
                                               TagOptions tagOptions) {
        String releaseBranch = ReadAction.compute(() -> ConfigUtil.getInitOptions(repository.getProject()).getReleaseBranch());
        // 判断目标分支是否存在
        GitCommandResult result = checkTargetBranchIsExist(repository, targetBranch);
        if (Objects.nonNull(result) && !result.success()) {
            return result;
        }

        // 发布完成拉取release最新代码
        if (Objects.nonNull(tagOptions)) {
            result = checkoutTargetBranchAndPull(repository, releaseBranch);
            if (!result.success()) {
                return result;
            }
        }

        // 切换到目标分支, pull最新代码
        result = checkoutTargetBranchAndPull(repository, targetBranch);
        if (!result.success()) {
            return result;
        }

        // 合并代码
        GitSimpleEventDetector mergeConflict = new GitSimpleEventDetector(GitSimpleEventDetector.Event.MERGE_CONFLICT);
        String sourceBranch = Objects.nonNull(tagOptions) ? releaseBranch : currentBranch;

        result = git.merge(repository, sourceBranch, targetBranch, mergeConflict);

        boolean allConflictsResolved = true;
        if (mergeConflict.hasHappened()) {
            // 解决冲突
            allConflictsResolved = new MyMergeConflictResolver(repository, currentBranch, targetBranch).merge();
        }

        if (!result.success() && !allConflictsResolved) {
            return result;
        }

        // 发布完成打tag
        if (Objects.nonNull(tagOptions)) {
            result = git.createNewTag(repository, tagOptions.getTagName(), tagOptions.getMessage());
            if (!result.success()) {
                return result;
            }
        }

        // push代码
        result = git.push(repository, targetBranch, false);
        if (!result.success()) {
            return result;
        }

        // 切换到当前分支
        return git.checkout(repository, currentBranch);
    }

    @Override
    public boolean lock(GitRepository repository, String currentBranch) {
        if (true) {
            mockLockFlag = true;
            return true;
        }

        GitCommandResult result = git.push(repository, currentBranch, Constants.LOCK_BRANCH_NAME, false);

        return result.success() && isNewBranch(result);
    }

    @Override
    public GitCommandResult unlock(GitRepository repository) {
        if (true) {
            mockLockFlag = false;
            return new GitCommandResult(false, 0, Collections.emptyList(), Collections.emptyList());
        }

        return git.deleteRemoteBranch(repository, Constants.LOCK_BRANCH_NAME);
    }

    @Override
    public boolean isLock(Project project) {
        if (true) {
            return mockLockFlag;
        }

        return GitBranchUtil.getRemoteBranches(project).contains(Constants.LOCK_BRANCH_NAME);
    }

    @Override
    public boolean isLock(GitRepository repository) {
        git.fetch(repository);
        repository.update();
        return isLock(repository.getProject());
    }

    @Override
    public void thirdPartyNotify(GitRepository repository) {
        try {
            String dingtalkToken = ConfigUtil.getInitOptions(repository.getProject()).getDingtalkToken();
            if (StringUtils.isNotBlank(dingtalkToken)) {
                String url = String.format("https://oapi.dingtalk.com/robot/send?access_token=%s", dingtalkToken);
                String msg = getRemoteLastCommit(repository, Constants.LOCK_BRANCH_NAME);

                msg = String.format(I18n.getContent(I18nKey.THIRD_PARTY_NOTIFY), repository.getProject().getName(), msg);
                OkHttpClientUtil.postApplicationJson(url, new DingtalkMessage(msg), "钉钉通知接口", String.class);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isExistChangeFile(@NotNull Project project) {
        Collection<Change> changes = ChangeListManager.getInstance(project).getAllChanges();
        if (CollectionUtils.isNotEmpty(changes)) {
            StringBuffer builder = new StringBuffer();
            changes.parallelStream().forEach(change -> builder.append(change.toString()).append("\r\n"));
            NotifyUtil.notifyError(project, "Error", String.format(I18n.getContent(I18nKey.CHANGE_FILE_VALVE$FILE_NOT_SUBMITTED) + ":\r\n %s", builder));
            return true;
        }
        return false;
    }


    @Override
    public String getUserEmail(GitRepository repository) {
        try {
            GitCommandResult result = git.getUserEmail(repository);
            String output = result.getOutputOrThrow(1);
            int pos = output.indexOf('\u0000');
            if (result.getExitCode() != 0 || pos == -1) {
                return "";
            }
            return output.substring(0, pos);
        } catch (VcsException e) {
            return "";
        }
    }

    @Override
    public boolean isExistTag(GitRepository repository, String tagName) {
        Set<String> myExistingTags = new HashSet<>();

        GitCommandResult result = git.tagList(repository);
        if (!result.success()) {
            GitUIUtil.showOperationError(repository.getProject(), GitBundle.message("tag.getting.existing.tags"), result.getErrorOutputAsJoinedString());
            throw new ProcessCanceledException();
        }
        for (StringScanner s = new StringScanner(result.getOutputAsJoinedString()); s.hasMoreData(); ) {
            String line = s.line();
            if (line.length() == 0) {
                continue;
            }
            myExistingTags.add(line);
        }

        return myExistingTags.contains(tagName);
    }

    @Override
    public GitCommandResult mergeRequest(GitRepository repository, String sourceBranch, String targetBranch, MergeRequestOptions mergeRequestOptions) {
        return git.mergeRequest(repository, sourceBranch, targetBranch, mergeRequestOptions);
    }

    private GitCommandResult checkTargetBranchIsExist(GitRepository repository, String
            targetBranch) {
        // 判断本地是否存在分支
        if (!GitBranchUtil.getLocalBranches(repository.getProject()).contains(targetBranch)) {
            if (GitBranchUtil.getRemoteBranches(repository.getProject()).contains(targetBranch)) {
                return git.checkoutNewBranch(repository, targetBranch);
            } else {
                String master = ConfigUtil.getInitOptions(repository.getProject()).getMasterBranch();
                return newNewBranchBaseRemoteMaster(repository, master, targetBranch);
            }
        }

        return null;
    }


    private boolean isNewBranch(GitCommandResult result) {
        return result.getOutputAsJoinedString().contains("new branch") || result.getErrorOutputAsJoinedString().contains("new branch");
    }

    private GitCommandResult checkoutTargetBranchAndPull(GitRepository repository, String targetBranch) {
        // 切换到目标分支
        git.checkout(repository, targetBranch);

        // pull最新代码
        return git.pull(repository, targetBranch);
    }

    private static class MyMergeConflictResolver extends GitMergeCommittingConflictResolver {
        String currentBranch;
        String targetBranch;

        MyMergeConflictResolver(GitRepository repository, String currentBranch, String targetBranch) {
            super(repository.getProject(), git4idea.commands.Git.getInstance(), new GitMerger(repository.getProject()),
                    GitUtil.getRootsFromRepositories(Lists.newArrayList(repository)), new Params(), true);
            this.currentBranch = currentBranch;
            this.targetBranch = targetBranch;
        }

        @Override
        protected void notifyUnresolvedRemain() {
            notifyWarning(I18n.getContent(I18nKey.MERGE_CONFLICT_TITLE), String.format(I18n.getContent(I18nKey.MERGE_CONFLICT_CONTENT), currentBranch, targetBranch));
        }
    }


}
