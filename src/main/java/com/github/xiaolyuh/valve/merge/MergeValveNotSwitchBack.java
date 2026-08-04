package com.github.xiaolyuh.valve.merge;

import com.github.xiaolyuh.i18n.I18n;
import com.github.xiaolyuh.i18n.I18nKey;
import com.github.xiaolyuh.service.ConfigService;
import com.github.xiaolyuh.utils.NotifyUtil;
import com.github.xiaolyuh.vo.TagOptions;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.project.Project;
import git4idea.commands.GitCommandResult;
import git4idea.repo.GitRepository;

import java.util.Objects;

/**
 * 分支合并阀门(合并完成后停留在目标分支)
 */
public class MergeValveNotSwitchBack extends Valve {
    private static final MergeValveNotSwitchBack valve = new MergeValveNotSwitchBack();

    public static Valve getInstance() {
        return valve;
    }

    @Override
    public boolean invoke(Project project, GitRepository repository, String sourceBranch, String targetBranch, TagOptions tagOptions) {
        GitCommandResult result = gitFlowPlus.mergeBranchAndPush(repository, sourceBranch, targetBranch, tagOptions, false);
        if (result.success()) {
            ConfigService configService = ConfigService.Companion.getInstance(project);

            String releaseBranch = ReadAction.compute(() -> configService.getInitOptions().getReleaseBranch());

            String source = Objects.nonNull(tagOptions) ? releaseBranch : sourceBranch;
            NotifyUtil.notifySuccess(project, "Success", I18n.getContent(I18nKey.MERGE_VALVE$MERGE_SUCCESS, source, targetBranch));

            return true;
        }

        NotifyUtil.notifyError(project, "Error", result.getErrorOutputAsJoinedString());

        return false;
    }
}
