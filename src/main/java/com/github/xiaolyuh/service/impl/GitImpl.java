package com.github.xiaolyuh.service.impl;

import com.github.xiaolyuh.consts.Constants;
import com.github.xiaolyuh.service.ConfigService;
import com.github.xiaolyuh.service.Git;
import com.github.xiaolyuh.utils.CollectionUtils;
import com.github.xiaolyuh.utils.NotifyUtil;
import com.github.xiaolyuh.vo.MergeRequestOptions;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.util.Key;
import git4idea.commands.GitCommand;
import git4idea.commands.GitCommandResult;
import git4idea.commands.GitLineHandler;
import git4idea.commands.GitLineHandlerListener;
import git4idea.i18n.GitBundle;
import git4idea.repo.GitRemote;
import git4idea.repo.GitRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author yuhao.wang3
 * @since 2020/4/7 14:36
 */
public class GitImpl implements Git {
    private final git4idea.commands.Git git = git4idea.commands.Git.getInstance();

    @NotNull
    @Override
    public GitCommandResult checkout(@NotNull GitRepository repository, @NotNull String reference) {
        // 切换分支
        NotifyUtil.notifyGitCommand(repository.getProject(), String.format("git -c core.quotepath=false -c log.showSignature=false checkout %s --force", reference));
        return git.checkout(repository, reference, null, true, false);
    }

    @Override
    public GitCommandResult checkoutNewBranch(GitRepository repository, String branchName) {
        // git checkout -b 本地分支名x origin/远程分支名x
        final GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(), GitCommand.CHECKOUT);
        h.setSilent(false);
        h.setStdoutSuppressed(false);
        h.addParameters("-b");
        h.addParameters(branchName);
        h.addParameters("origin/" + branchName);
        NotifyUtil.notifyGitCommand(repository.getProject(), h.printableCommandLine());
        return git.runCommand(h);
    }

    @Override
    public GitCommandResult fetchNewBranchByRemoteMaster(GitRepository repository, String master, String newBranchName) {
        //git fetch origin 远程分支名x:本地分支名x
        GitRemote remote = getDefaultRemote(repository);
        GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(), GitCommand.FETCH);
        h.setSilent(false);
        h.setStdoutSuppressed(false);
        h.setUrls(remote.getUrls());
        h.addParameters("origin");
        // 远程分支名x:本地分支名x
        h.addParameters(master + ":" + newBranchName);
        h.addParameters("-f");

        NotifyUtil.notifyGitCommand(repository.getProject(), h.printableCommandLine());
        return git.runCommand(h);
    }

    @Override
    public GitCommandResult branch(@NotNull GitRepository repository, @NotNull String newBranchName) {
        //git branch 本地分支名x
        GitRemote remote = getDefaultRemote(repository);
        GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(), GitCommand.BRANCH);
        h.setSilent(false);
        h.setStdoutSuppressed(false);
        h.setUrls(remote.getUrls());
        // 远程分支名x:本地分支名x
        h.addParameters(newBranchName);

        NotifyUtil.notifyGitCommand(repository.getProject(), h.printableCommandLine());
        return git.runCommand(h);
    }

    @Override
    public GitCommandResult renameBranch(@NotNull GitRepository repository,
                                         @NotNull String oldBranch,
                                         @NotNull String newBranchName) {

        return git.renameBranch(repository, oldBranch, newBranchName);
    }

    @Override
    public GitCommandResult push(GitRepository repository, String localBranchName, boolean isNewBranch) {
        return push(repository, localBranchName, localBranchName, isNewBranch);
    }

    /**
     * push 本地分支到远程
     *
     * @param repository       gitRepository
     * @param localBranchName  分支名称
     * @param remoteBranchName 是否是新建分支
     */
    @Override
    public GitCommandResult push(GitRepository repository, String localBranchName, String remoteBranchName, boolean isNewBranch) {
        GitRemote remote = getDefaultRemote(repository);
        GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(), GitCommand.PUSH);
        h.setSilent(false);
        h.setStdoutSuppressed(false);
        h.setUrls(remote.getUrls());
        h.addParameters("origin");
        h.addParameters(localBranchName + ":" + remoteBranchName);
        h.addParameters("--tag");
        if (isNewBranch) {
            h.addParameters("--set-upstream");
        }

        NotifyUtil.notifyGitCommand(repository.getProject(), h.printableCommandLine());
        return git.runCommand(h);
    }

    @Override
    public GitCommandResult deleteRemoteBranch(@NotNull GitRepository repository, @Nullable String branchName) {
        GitRemote remote = getDefaultRemote(repository);
        GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(), GitCommand.PUSH);
        h.setSilent(false);
        h.setStdoutSuppressed(false);
        h.setUrls(remote.getUrls());
        h.addParameters("origin");
        h.addParameters("--delete");
        h.addParameters(branchName);

        NotifyUtil.notifyGitCommand(repository.getProject(), h.printableCommandLine());
        return git.runCommand(h);
    }

    @Override
    public GitCommandResult deleteLocalBranch(@NotNull GitRepository repository, @NotNull String branchName) {
        // 删除本地分支
        NotifyUtil.notifyGitCommand(repository.getProject(), String.format("git -c core.quotepath=false -c log.showSignature=false branch -D %s", branchName));
        return git.branchDelete(repository, branchName, true);
    }

    @Override
    public GitCommandResult showRemoteLastCommit(@NotNull GitRepository repository, @Nullable String remoteBranchName) {
        //git show origin/master -s --format=Author:%ae-Date:%ad-Message:%s --date=format:%Y-%m-%d_%H:%M:%S
        GitRemote remote = getDefaultRemote(repository);
        GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(), GitCommand.SHOW);
        h.setSilent(false);
        h.setStdoutSuppressed(false);
        h.setUrls(remote.getUrls());
        h.addParameters("origin/" + remoteBranchName);
        h.addParameters("-s");
        h.addParameters("--format=Author:%ae-Message:%s");

        NotifyUtil.notifyGitCommand(repository.getProject(), h.printableCommandLine());
        return git.runCommand(h);
    }

    @Override
    public GitCommandResult showLocalLastCommit(@NotNull GitRepository repository, @Nullable String localBranchName) {
        //git show master  -s --format=%s-body:%b
        GitRemote remote = getDefaultRemote(repository);
        GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(), GitCommand.SHOW);
        h.setSilent(false);
        h.setStdoutSuppressed(false);
        h.setUrls(remote.getUrls());
        h.addParameters(localBranchName);
        h.addParameters("-s");
        h.addParameters("--format=%s-body:%b");

        NotifyUtil.notifyGitCommand(repository.getProject(), h.printableCommandLine());
        return git.runCommand(h);
    }

    @Override
    public GitCommandResult getLastReleaseTime(@NotNull GitRepository repository) {
        //git reflog show --date=iso <branch name>
        GitRemote remote = getDefaultRemote(repository);
        GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(), getReadGitCommand());
        h.setSilent(false);
        h.setStdoutSuppressed(false);
        h.setUrls(remote.getUrls());
        h.addParameters("show");
        h.addParameters("--date=iso");
        h.addParameters("origin/" + Constants.LOCK_BRANCH_NAME);

        NotifyUtil.notifyGitCommand(repository.getProject(), h.printableCommandLine());
        return git.runCommand(h);
    }

    @Override
    public GitCommandResult getAllBranchList(GitRepository repository) {
        GitRemote remote = this.getDefaultRemote(repository);
        GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(), GitCommand.BRANCH);
        h.setSilent(false);
        h.setStdoutSuppressed(false);
        h.setUrls(remote.getUrls());
        h.addParameters("-a");
        h.addParameters("--sort", "committerdate");
        h.addParameters("--format", "%(committerdate:short)@@@%(authorname)@@@%(refname:short)");
        NotifyUtil.notifyGitCommand(repository.getProject(), h.printableCommandLine());
        return git.runCommand(h);
    }

    @Override
    public GitCommandResult getMergedBranchList(GitRepository repository, String date) {
        GitRemote remote = this.getDefaultRemote(repository);
        GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(), GitCommand.LOG);
        h.setSilent(false);
        h.setStdoutSuppressed(false);
        h.setUrls(remote.getUrls());
        h.addParameters("origin/master", "-i", "-10000");
        h.addParameters(String.format("--after=%s", date));
        h.addParameters("--grep=Merge");
        h.addParameters("--format=%s-body:%b");
        NotifyUtil.notifyGitCommand(repository.getProject(), h.printableCommandLine());
        return this.git.runCommand(h);
    }

    @Override
    public GitCommandResult createNewTag(@NotNull GitRepository repository, @Nullable String tagName, @Nullable String message) {
        final GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(), GitCommand.TAG);
        h.setSilent(false);
        h.addParameters("-a");
        h.addParameters("-f");
        h.addParameters("-m");
        h.addParameters(message);
        ConfigService configService = ConfigService.Companion.getInstance(repository.getProject());
        h.addParameters(configService.getInitOptions().getTagPrefix() + tagName);

        NotifyUtil.notifyGitCommand(repository.getProject(), h.printableCommandLine());
        return git.runCommand(h);
    }

    @Override
    public GitCommandResult tagList(@NotNull GitRepository repository) {
        GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(), GitCommand.TAG);
        h.setSilent(true);
        NotifyUtil.notifyGitCommand(repository.getProject(), h.printableCommandLine());

        return ProgressManager.getInstance()
                .runProcessWithProgressSynchronously(() -> git.runCommand(h),
                        GitBundle.message("tag.getting.existing.tags"),
                        false,
                        repository.getProject());
    }

    @Override
    public GitCommandResult fetch(@NotNull GitRepository repository) {
        NotifyUtil.notifyGitCommand(repository.getProject(), "git -c core.quotepath=false -c log.showSignature=false fetch origin");
        return git.fetch(repository, getDefaultRemote(repository), Collections.singletonList(new GitFetchPruneDetector()));
    }

    @Override
    public GitCommandResult pull(GitRepository repository, @Nullable String branchName) {
        GitRemote remote = getDefaultRemote(repository);
        GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(), GitCommand.PULL);
        h.setSilent(false);
        h.setStdoutSuppressed(false);
        h.setUrls(remote.getUrls());
        h.addParameters("origin");
        h.addParameters(branchName + ":" + branchName);

        NotifyUtil.notifyGitCommand(repository.getProject(), h.printableCommandLine());
        return git.runCommand(h);
    }

    @Override
    public GitCommandResult merge(@NotNull GitRepository repository, @NotNull String sourceBranch, @Nullable String targetBranch, @NotNull GitLineHandlerListener... listeners) {
        NotifyUtil.notifyGitCommand(repository.getProject(),
                String.format("git -c core.quotepath=false -c log.showSignature=false merge %s -m \"Merge branch '%s' into %s\"", sourceBranch, sourceBranch, targetBranch));
        List<String> params = new ArrayList<>();
        params.add("-m");
        params.add(String.format("Merge branch '%s' into %s", sourceBranch, targetBranch));
        return git.merge(repository, sourceBranch, params, listeners);
    }

    @Override
    public GitCommandResult getUserEmail(GitRepository repository) {
        GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(), GitCommand.CONFIG);
        h.setSilent(true);
        h.addParameters("--null", "--get", "user.email");
        NotifyUtil.notifyGitCommand(repository.getProject(), h.printableCommandLine());
        return git.runCommand(h);
    }

    @Override
    public GitCommandResult mergeRequest(GitRepository repository, String sourceBranch, String targetBranch, MergeRequestOptions mergeRequestOptions) {
        // git push -o merge_request.create -o merge_request.target=%s -o merge_request.title=%s -o merge_request.description=%s -o merge_request.label="label1"
        GitRemote remote = getDefaultRemote(repository);
        GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(), GitCommand.PUSH);
        h.setSilent(false);
        h.setStdoutSuppressed(false);
        h.setUrls(remote.getUrls());
        h.addParameters("origin");
        h.addParameters(sourceBranch + ":" + sourceBranch);
        h.addParameters("--set-upstream");

        h.addParameters("-o", "merge_request.create");
        h.addParameters("-o", "merge_request.target=" + targetBranch);
        h.addParameters("-o", "merge_request.remove_source_branch");
        h.addParameters("-o", String.format("merge_request.label=%s", mergeRequestOptions.getTitle().split("\\(")[0]));
        h.addParameters("-o", String.format("merge_request.title=%s", mergeRequestOptions.getTitle()));
        h.addParameters("-o", String.format("merge_request.description=%s", mergeRequestOptions.getMessage()));

        NotifyUtil.notifyGitCommand(repository.getProject(), h.printableCommandLine());
        return git.runCommand(h);
    }

    private GitRemote getDefaultRemote(@NotNull GitRepository repository) {
        Collection<GitRemote> remotes = repository.getRemotes();
        if (CollectionUtils.isEmpty(remotes)) {
            throw new RuntimeException("no default remotes found");
        }

        return remotes.iterator().next();
    }

    private GitCommand getReadGitCommand() {
        try {
            Method m = GitCommand.class.getDeclaredMethod("read", String.class);
            m.setAccessible(true);
            return (GitCommand) m.invoke(null, "reflog");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static class GitFetchPruneDetector implements GitLineHandlerListener {

        private final Pattern PRUNE_PATTERN = Pattern.compile("\\s*x\\s*\\[deleted\\].*->\\s*(\\S*)");

        @NotNull
        private final Collection<String> myPrunedRefs = new ArrayList<>();

        @Override
        public void onLineAvailable(String line, Key outputType) {
            //  x [deleted]         (none)     -> origin/frmari
            Matcher matcher = PRUNE_PATTERN.matcher(line);
            if (matcher.matches()) {
                myPrunedRefs.add(matcher.group(1));
            }
        }

        @NotNull
        public Collection<String> getPrunedRefs() {
            return myPrunedRefs;
        }
    }
}
