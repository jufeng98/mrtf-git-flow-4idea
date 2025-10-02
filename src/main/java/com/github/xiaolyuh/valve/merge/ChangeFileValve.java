package com.github.xiaolyuh.valve.merge;

import com.github.xiaolyuh.i18n.I18n;
import com.github.xiaolyuh.i18n.I18nKey;
import com.github.xiaolyuh.utils.CollectionUtils;
import com.github.xiaolyuh.utils.NotifyUtil;
import com.github.xiaolyuh.vo.TagOptions;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ChangeListManager;
import git4idea.repo.GitRepository;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * 检查是否有未提交文件
 *
 * @author yuhao.wang3
 * @since 2020/4/7 16:42
 */
public class ChangeFileValve extends Valve {
    private static final ChangeFileValve valve = new ChangeFileValve();

    public static Valve getInstance() {
        return valve;
    }

    @Override
    public boolean invoke(Project project, GitRepository repository, String sourceBranch, String targetBranch, TagOptions tagOptions) {
        Collection<Change> changes = ChangeListManager.getInstance(project).getAllChanges();
        if (CollectionUtils.isEmpty(changes)) {
            return true;
        }

        String desc = changes.stream()
                .map(Change::toString)
                .limit(8)
                .collect(Collectors.joining("、"));

        NotifyUtil.notifyError(project, "Error", I18n.getContent(I18nKey.CHANGE_FILE_VALVE$FILE_NOT_SUBMITTED, desc));

        return false;
    }
}
