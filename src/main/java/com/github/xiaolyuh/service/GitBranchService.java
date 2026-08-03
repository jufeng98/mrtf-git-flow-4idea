package com.github.xiaolyuh.service;

import com.github.xiaolyuh.consts.Constants;
import com.github.xiaolyuh.logger.GitFlowPlusLogger;
import com.github.xiaolyuh.utils.CollectionUtils;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import git4idea.*;
import git4idea.branch.GitBranchUtil;
import git4idea.repo.GitRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * GIT分支管理工具
 *
 * @author yuhao.wang3
 * @since 2020/3/17 15:16
 */
@Service(Service.Level.PROJECT)
public final class GitBranchService {
    private volatile Boolean gitProject;
    private volatile GitRepository currentRepository;

    public static GitBranchService getInstance(Project project) {
        return project.getService(GitBranchService.class);
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
                .sorted()
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
        GitBranchService gitBranchService = getInstance(project);

        if (gitBranchService.gitProject != null) {
            return gitBranchService.gitProject;
        }

        List<GitRepository> repositories = GitUtil.getRepositoryManager(project).getRepositories();
        if (CollectionUtils.isEmpty(repositories)) {
            return false;
        }

        gitBranchService.gitProject = true;
        GitFlowPlusLogger.INSTANCE.logInfo("当前属于 git 项目:" + repositories);

        return true;
    }

    /**
     * 获取当前仓库
     *
     * @param project project
     */
    public static @Nullable GitRepository getCurrentRepository(@NotNull Project project) {
        GitBranchService gitBranchService = getInstance(project);

        GitRepository currentRepository = gitBranchService.currentRepository;
        if (currentRepository != null) {
            return currentRepository;
        }

        String filePath = project.getBasePath() + File.separator + Constants.CONFIG_FILE_NAME;
        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(filePath);
        if (virtualFile == null) {
            FileEditor fileEditor = FileEditorManager.getInstance(project).getSelectedEditor();
            if (fileEditor != null) {
                virtualFile = fileEditor.getFile();
            }
        }

        if (virtualFile == null) {
            return null;
        }

        GitRepository repository = GitBranchUtil.guessWidgetRepository(project, virtualFile);
        if (repository == null) {
            return null;
        }

        gitBranchService.currentRepository = repository;
        GitFlowPlusLogger.INSTANCE.logInfo("当前仓库为:" + repository);

        return repository;
    }
}
