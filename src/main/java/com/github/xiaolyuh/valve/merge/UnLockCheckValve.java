package com.github.xiaolyuh.valve.merge;

import com.github.xiaolyuh.consts.Constants;
import com.github.xiaolyuh.vo.TagOptions;
import com.github.xiaolyuh.i18n.I18n;
import com.github.xiaolyuh.i18n.I18nKey;
import com.github.xiaolyuh.utils.NotifyUtil;
import com.intellij.openapi.project.Project;
import git4idea.repo.GitRepository;

/**
 * 解锁校验
 *
 * @author yuhao.wang3
 * @since 2020/4/7 16:42
 */
public class UnLockCheckValve extends Valve {
    private static final UnLockCheckValve valve = new UnLockCheckValve();

    public static Valve getInstance() {
        return valve;
    }

    @Override
    public boolean invoke(Project project, GitRepository repository, String sourceBranch, String targetBranch, TagOptions tagOptions) {
        if (true) {
            return true;
        }

        String lastCommitMsg = gitFlowPlus.getRemoteLastCommit(repository, Constants.LOCK_BRANCH_NAME);
        String email = gitFlowPlus.getUserEmail(repository);
        // 校验操作人
        if (!lastCommitMsg.contains(email)) {
            NotifyUtil.notifyError(project, "Error", I18n.getContent(I18nKey.LOCK_VALVE$LOCKED, lastCommitMsg));
            return false;
        }

        // 校验锁定状态
        if (!gitFlowPlus.isLock(repository)) {
            NotifyUtil.notifyError(project, "Error", I18n.getContent(I18nKey.UN_LOCK_CHECK_VALVE$UN_LOCKED));
            return false;
        }
        return true;
    }
}
