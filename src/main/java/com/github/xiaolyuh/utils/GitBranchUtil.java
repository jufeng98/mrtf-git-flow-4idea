package com.github.xiaolyuh.utils;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import git4idea.GitLocalBranch;
import git4idea.GitReference;
import git4idea.GitRemoteBranch;
import git4idea.GitUtil;
import git4idea.repo.GitRepository;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * GIT分支管理工具
 *
 * @author yuhao.wang3
 * @since 2020/3/17 15:16
 */
@Service(Service.Level.PROJECT)
public final class GitBranchUtil {
    private Boolean gitProject;
    private GitRepository currentRepository;

    public static GitBranchUtil getInstance(Project project) {
        return project.getService(GitBranchUtil.class);
    }

    /**
     * 获取远程分支名
     *
     * @param project {@link Project}
     * @return List<String>
     */
    public static List<String> getRemoteBranches(Project project) {
        // 获取仓库名称
        List<GitRepository> gitRepositories = GitUtil.getRepositoryManager(project).getRepositories();
        if (CollectionUtils.isEmpty(gitRepositories)) {
            return Collections.emptyList();
        }

        Collection<GitRemoteBranch> remoteBranches = gitRepositories.get(0).getBranches().getRemoteBranches();

        return remoteBranches.parallelStream()
                .map(GitRemoteBranch::getNameForRemoteOperations)
                .collect(Collectors.toList());

    }

    /**
     * 获取远程分支名
     *
     * @param project {@link Project}
     * @return List<String>
     */
    public static List<String> getLocalBranches(Project project) {
        // 获取仓库名称
        List<GitRepository> gitRepositories = GitUtil.getRepositoryManager(project).getRepositories();
        if (CollectionUtils.isEmpty(gitRepositories)) {
            return Collections.emptyList();
        }

        Collection<GitLocalBranch> localBranches = gitRepositories.get(0).getBranches().getLocalBranches();

        return localBranches.parallelStream()
                .map(GitReference::getName)
                .collect(Collectors.toList());
    }

    /**
     * 判断是否是git项目
     *
     * @param project {@link Project}
     * @return boolean
     */
    public static boolean isGitProject(Project project) {
        GitBranchUtil gitBranchUtil = getInstance(project);

        Boolean match = gitBranchUtil.gitProject;

        if (match != null) {
            return match;
        }

        match = CollectionUtils.isNotEmpty(GitUtil.getRepositoryManager(project).getRepositories());

        gitBranchUtil.gitProject = match;

        return match;
    }

    /**
     * 获取当前仓库
     *
     * @param project project
     */
    public static GitRepository getCurrentRepository(@NotNull Project project) {
        GitBranchUtil gitBranchUtil = getInstance(project);

        GitRepository currentRepository = gitBranchUtil.currentRepository;

        if (currentRepository != null) {
            return currentRepository;
        }

        //noinspection deprecation
        currentRepository = git4idea.branch.GitBranchUtil.getCurrentRepository(project);

        gitBranchUtil.currentRepository = currentRepository;

        return currentRepository;
    }
}
