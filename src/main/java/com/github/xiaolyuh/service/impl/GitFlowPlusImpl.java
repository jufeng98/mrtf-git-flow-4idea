package com.github.xiaolyuh.service.impl;

import com.github.xiaolyuh.config.InitOptions;
import com.github.xiaolyuh.consts.Constants;
import com.github.xiaolyuh.i18n.I18n;
import com.github.xiaolyuh.i18n.I18nKey;
import com.github.xiaolyuh.service.ConfigService;
import com.github.xiaolyuh.service.Git;
import com.github.xiaolyuh.service.GitFlowPlus;
import com.github.xiaolyuh.service.HttpClientService;
import com.github.xiaolyuh.utils.CollectionUtils;
import com.github.xiaolyuh.service.GitBranchService;
import com.github.xiaolyuh.utils.NotifyUtil;
import com.github.xiaolyuh.utils.StringUtils;
import com.github.xiaolyuh.vo.BranchVo;
import com.github.xiaolyuh.vo.DingtalkMessage;
import com.github.xiaolyuh.vo.MergeRequestOptions;
import com.github.xiaolyuh.vo.TagOptions;
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
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.xiaolyuh.consts.Constants.DATE_PATTERN;

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
            @SuppressWarnings("deprecation")
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
    public GitCommandResult newNewBranchByLocalBranch(@NotNull GitRepository repository, String localBranchName, @NotNull String newBranchName) {
        git.checkout(repository, localBranchName);
        git.branch(repository, newBranchName);
        return git.checkout(repository, newBranchName);
    }

    @Override
    public GitCommandResult deleteBranch(@NotNull GitRepository repository,
                                         String checkoutBranchName,
                                         String branchName) {
        git.checkout(repository, checkoutBranchName);
        git.deleteRemoteBranch(repository, branchName);
        return git.deleteLocalBranch(repository, branchName);
    }

    public void deleteBranch(@NotNull GitRepository repository,
                             @NotNull String branchName,
                             boolean isDeleteLocalBranch) {
        if (isDeleteLocalBranch) {
            git.deleteLocalBranch(repository, branchName);
        }
        git.deleteRemoteBranch(repository, branchName);
    }

    @Override
    public GitCommandResult deleteLocalBranch(@NotNull GitRepository repository,
                                              String checkoutBranchName,
                                              String branchName) {

        git.checkout(repository, checkoutBranchName);
        return git.deleteLocalBranch(repository, branchName);
    }

    @Override
    public String getCurrentBranch(@NotNull Project project) {
        GitRepository repository = GitBranchService.getCurrentRepository(project);
        //noinspection DataFlowIssue
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
    public List<BranchVo> getBranchList(GitRepository repository) {
        ConfigService configService = ConfigService.Companion.getInstance(repository.getProject());

        GitCommandResult gitCommandResult = git.getAllBranchList(repository);

        List<String> output = gitCommandResult.getOutput();
        return output.stream()
                .map((row) -> row.replace("origin/", ""))
                .filter((row) -> {
                    if (!configService.isInit()) {
                        return true;
                    }

                    String[] msg = row.split("@@@");
                    InitOptions initOptions = configService.getInitOptions();
                    if (msg[2].equalsIgnoreCase(initOptions.getMasterBranch())) {
                        return false;
                    } else if (msg[2].equalsIgnoreCase(initOptions.getReleaseBranch())) {
                        return false;
                    } else if (msg[2].equalsIgnoreCase(initOptions.getTestBranch())) {
                        return false;
                    } else if (msg[2].equalsIgnoreCase("HEAD")) {
                        return false;
                    } else {
                        return !msg[2].endsWith("_mr");
                    }
                }).map((row) -> {
                    String[] msg = row.split("@@@");
                    BranchVo branchVo = new BranchVo();
                    try {
                        branchVo.setLastCommitDate(DateUtils.parseDate(msg[0], DATE_PATTERN));
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    branchVo.setCreateUser(msg[1]);
                    branchVo.setBranch(msg[2]);
                    return branchVo;
                }).distinct()
                .sorted(Comparator.comparing(BranchVo::getLastCommitDate))
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getMergedBranchList(GitRepository repository) {
        String date = DateFormatUtils.format(DateUtils.addYears(new Date(), -2), DATE_PATTERN);
        GitCommandResult branchList = git.getMergedBranchList(repository, date);
        List<String> output = branchList.getOutput();
        return output.stream()
                .filter(StringUtils::isNotBlank)
                .filter((message) -> message.startsWith("Merge branch"))
                .map((row) -> {
                    String[] msg = row.replace("'", "").split(" ");
                    return msg.length < 3 ? null : msg[2].replace("_mr", "");
                })
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public GitCommandResult mergeBranchAndPush(@NotNull GitRepository repository, String currentBranch, String targetBranch,
                                               TagOptions tagOptions) {
        String releaseBranch = ReadAction.compute(() -> {
            ConfigService configService = ConfigService.Companion.getInstance(repository.getProject());
            return configService.getInitOptions().getReleaseBranch();
        });
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
        //noinspection deprecation
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
        //noinspection ConstantValue
        if (true) {
            mockLockFlag = true;
            return true;
        }

        GitCommandResult result = git.push(repository, currentBranch, Constants.LOCK_BRANCH_NAME, false);

        return result.success() && isNewBranch(result);
    }

    @Override
    public GitCommandResult unlock(GitRepository repository) {
        //noinspection ConstantValue
        if (true) {
            mockLockFlag = false;
            return new GitCommandResult(false, 0, Collections.emptyList(), Collections.emptyList());
        }

        return git.deleteRemoteBranch(repository, Constants.LOCK_BRANCH_NAME);
    }

    @Override
    public boolean isLock(Project project) {
        //noinspection ConstantValue
        if (true) {
            return mockLockFlag;
        }

        return GitBranchService.getRemoteBranches(project).contains(Constants.LOCK_BRANCH_NAME);
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
            Project project = repository.getProject();
            ConfigService configService = ConfigService.Companion.getInstance(project);
            String dingtalkToken = configService.getInitOptions().getDingtalkToken();
            if (StringUtils.isNotBlank(dingtalkToken)) {
                String url = String.format("https://oapi.dingtalk.com/robot/send?access_token=%s", dingtalkToken);
                String msg = getRemoteLastCommit(repository, Constants.LOCK_BRANCH_NAME);

                msg = I18n.getContent(I18nKey.THIRD_PARTY_NOTIFY, project.getName(), msg);

                HttpClientService httpClientService = HttpClientService.Companion.getInstance(project);
                httpClientService.postApplicationJson(url, new DingtalkMessage(msg), String.class);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isExistChangeFile(@NotNull Project project) {
        Collection<Change> changes = ChangeListManager.getInstance(project).getAllChanges();
        if (CollectionUtils.isEmpty(changes)) {
            return false;
        }

        String desc = changes.stream()
                .map(Change::toString)
                .limit(8)
                .collect(Collectors.joining("、"));

        NotifyUtil.notifyError(project, "Error", I18n.getContent(I18nKey.CHANGE_FILE_VALVE$FILE_NOT_SUBMITTED, desc));

        return true;
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
            if (line.isEmpty()) {
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
        if (!GitBranchService.getLocalBranches(repository.getProject()).contains(targetBranch)) {
            if (GitBranchService.getRemoteBranches(repository.getProject()).contains(targetBranch)) {
                return git.checkoutNewBranch(repository, targetBranch);
            } else {
                ConfigService configService = ConfigService.Companion.getInstance(repository.getProject());
                String master = configService.getInitOptions().getMasterBranch();
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
            notifyWarning(I18n.getContent(I18nKey.MERGE_CONFLICT_TITLE), I18n.getContent(I18nKey.MERGE_CONFLICT_CONTENT, currentBranch, targetBranch));
        }
    }


}
