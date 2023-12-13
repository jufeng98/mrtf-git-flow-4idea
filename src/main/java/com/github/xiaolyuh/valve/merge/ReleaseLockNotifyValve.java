package com.github.xiaolyuh.valve.merge;

import com.github.xiaolyuh.vo.TagOptions;
import com.intellij.openapi.project.Project;
import git4idea.repo.GitRepository;

/**
 * 发布分支锁定通知阀门（钉钉）
 *
 * @author yuhao.wang3
 * @since 2020/4/7 16:42
 */
public class ReleaseLockNotifyValve extends Valve {
    private static final ReleaseLockNotifyValve valve = new ReleaseLockNotifyValve();

    public static Valve getInstance() {
        return valve;
    }

    @Override
    public boolean invoke(Project project, GitRepository repository, String sourceBranch, String targetBranch, TagOptions tagOptions) {
        gitFlowPlus.thirdPartyNotify(repository);
        return true;
    }
}
