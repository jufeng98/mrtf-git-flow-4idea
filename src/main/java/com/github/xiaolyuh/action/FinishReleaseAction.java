package com.github.xiaolyuh.action;

import com.github.xiaolyuh.i18n.I18n;
import com.github.xiaolyuh.i18n.I18nKey;
import com.github.xiaolyuh.ui.TagDialog;
import com.github.xiaolyuh.utils.ConfigUtil;
import com.github.xiaolyuh.valve.merge.*;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.util.ReflectionUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 发布完成
 *
 * @author yuhao.wang3
 */
public class FinishReleaseAction extends AbstractMergeAction {

    public FinishReleaseAction() {
        super("发布完成", "解锁，并将发布分支合并到主干分支",
                IconLoader.getIcon("/icons/finished.svg", Objects.requireNonNull(ReflectionUtil.getGrandCallerClass())));
    }

    @Override
    protected void setEnabledAndText(AnActionEvent event) {
        event.getPresentation().setText(I18n.getContent(I18nKey.FINISH_RELEASE_ACTION$TEXT));
        if (event.getPresentation().isEnabled()) {
            event.getPresentation().setEnabled(gitFlowPlus.isLock(event.getProject()));
        }
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getProject();

        TagDialog tagDialog = new TagDialog(project);
        tagDialog.show();
        if (tagDialog.isOK()) {
            super.actionPerformed(event, tagDialog.getTagOptions());
        }
    }

    @Override
    protected String getDialogContent(Project project, boolean isStartTest) {
        String release = ConfigUtil.getConfig(project).get().getReleaseBranch();
        return String.format(I18n.getContent(I18nKey.MERGE_BRANCH_MSG), release, getTargetBranch(project));
    }

    @Override
    protected String getTargetBranch(Project project) {
        return ConfigUtil.getConfig(project).get().getMasterBranch();
    }

    @Override
    protected String getDialogTitle(Project project) {
        return I18n.getContent(I18nKey.FINISH_RELEASE_ACTION$DIALOG_TITLE);
    }

    @Override
    protected String getTaskTitle(Project project) {
        String release = ConfigUtil.getConfig(project).get().getReleaseBranch();
        return String.format(I18n.getContent(I18nKey.MERGE_BRANCH_TASK_TITLE), release, getTargetBranch(project));
    }

    @Override
    protected List<Valve> getValves() {
        List<Valve> valves = new ArrayList<>();
        valves.add(ChangeFileValve.getInstance());
        valves.add(UnLockCheckValve.getInstance());
        valves.add(MergeValve.getInstance());
        valves.add(UnLockValve.getInstance());
        return valves;
    }
}
