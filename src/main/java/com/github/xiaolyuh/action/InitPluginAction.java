package com.github.xiaolyuh.action;

import com.github.xiaolyuh.config.InitOptions;
import com.github.xiaolyuh.i18n.I18n;
import com.github.xiaolyuh.i18n.I18nKey;
import com.github.xiaolyuh.icons.GitFlowPlusIcons;
import com.github.xiaolyuh.service.ConfigService;
import com.github.xiaolyuh.service.GitFlowPlus;
import com.github.xiaolyuh.ui.InitPluginDialog;
import com.github.xiaolyuh.utils.GitBranchUtil;
import com.github.xiaolyuh.utils.GsonUtils;
import com.github.xiaolyuh.utils.NotifyUtil;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import git4idea.commands.GitCommandResult;
import git4idea.repo.GitRepository;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * 初始化Action
 *
 * @author yuhao.wang3
 */
public class InitPluginAction extends AnAction {

    @SuppressWarnings("ActionPresentationInstantiatedInCtor")
    public InitPluginAction() {
        super(I18n.nls("action.init.txt"), I18n.nls("action.init.desc"), GitFlowPlusIcons.INSTANCE.getConfig());
    }

    private final GitFlowPlus gitFlowPlus = GitFlowPlus.getInstance();

    @Override
    public void update(@NotNull AnActionEvent event) {
        super.update(event);
        Project project = event.getProject();
        if (project == null) {
            return;
        }

        event.getPresentation().setEnabledAndVisible(GitBranchUtil.isGitProject(project));

        ConfigService configService = ConfigService.Companion.getInstance(project);

        event.getPresentation().setText(configService.isInit()
                ? I18n.getContent(I18nKey.INIT_PLUGIN_ACTION$TEXT_UPDATE) : I18n.getContent(I18nKey.INIT_PLUGIN_ACTION$TEXT_INIT));
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        final Project project = event.getProject();
        @SuppressWarnings("ConstantConditions")
        GitRepository repository = GitBranchUtil.getCurrentRepository(project);
        if (Objects.isNull(repository)) {
            return;
        }

        InitPluginDialog initPluginDialog = new InitPluginDialog(project);
        initPluginDialog.show();

        if (!initPluginDialog.isOK()) {
            return;
        }

        InitOptions initOptions = initPluginDialog.getOptions();

        new Task.Backgroundable(project, "Init gitFlowPlus plugins", false) {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                NotifyUtil.notifyGitCommand(event.getProject(), "=============================================");

                // 校验主干分支是否存在
                List<String> remoteBranches = GitBranchUtil.getRemoteBranches(project);
                if (!remoteBranches.contains(initOptions.getMasterBranch())) {
                    String msg = I18n.getContent(I18nKey.INIT_PLUGIN_ACTION$NOT_EXIST_MASTER_INFO, initOptions.getMasterBranch());
                    NotifyUtil.notifyError(myProject, "Error", msg);
                    return;
                }

                // 校验主测试支是否存在，不存在就新建
                if (!remoteBranches.contains(initOptions.getTestBranch())) {
                    GitCommandResult result = gitFlowPlus.newNewBranchBaseRemoteMaster(repository, initOptions.getMasterBranch(),
                            initOptions.getTestBranch());
                    if (result.success()) {
                        String msg = I18n.getContent(I18nKey.NEW_BRANCH_SUCCESS, initOptions.getMasterBranch(), initOptions.getTestBranch());
                        NotifyUtil.notifySuccess(myProject, "Success", msg);
                    } else {
                        String msg = I18n.getContent(I18nKey.INIT_PLUGIN_ACTION$INIT_FAILURE, result.getErrorOutputAsJoinedString());
                        NotifyUtil.notifyError(myProject, "Error", msg);
                        return;
                    }
                }

                // 校验主发布支是否存在，不存在就新建
                if (!remoteBranches.contains(initOptions.getReleaseBranch())) {
                    // 新建分支发布分支
                    GitCommandResult result = gitFlowPlus.newNewBranchBaseRemoteMaster(repository, initOptions.getMasterBranch(),
                            initOptions.getReleaseBranch());
                    if (result.success()) {
                        String msg = I18n.getContent(I18nKey.NEW_BRANCH_SUCCESS, initOptions.getMasterBranch(), initOptions.getReleaseBranch());
                        NotifyUtil.notifySuccess(myProject, "Success", msg);
                    } else {
                        String msg = I18n.getContent(I18nKey.INIT_PLUGIN_ACTION$INIT_FAILURE, result.getErrorOutputAsJoinedString());
                        NotifyUtil.notifyError(myProject, "Error", msg);
                        return;
                    }
                }

                ConfigService configService = ConfigService.Companion.getInstance(project);
                configService.saveKubesphereUser(initOptions.getKubesphereUsername(), initOptions.getKubespherePassword());
                configService.saveFsWebHookUrl(initOptions.getFsWebHookUrl());

                // 存储配置
                String configJson = GsonUtils.INSTANCE.getGson().toJson(initOptions);
                configService.saveConfigToLocal(configJson);
                configService.saveConfigToFile(configJson, configService::tryInitConfig);

                // 将配置文件加入GIT管理
                gitFlowPlus.addConfigToGit(repository);

                NotifyUtil.notifySuccess(myProject, "Success", I18n.getContent(I18nKey.INIT_PLUGIN_ACTION$INIT_SUCCESS));

                //update the widget
                myProject.getMessageBus().syncPublisher(GitRepository.GIT_REPO_CHANGE).repositoryChanged(repository);

                repository.update();
            }
        }.queue();
    }

}
