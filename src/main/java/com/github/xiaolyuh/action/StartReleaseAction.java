package com.github.xiaolyuh.action;

import com.github.xiaolyuh.GitFlowPlus;
import com.github.xiaolyuh.i18n.I18n;
import com.github.xiaolyuh.i18n.I18nKey;
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
 * 开始发布
 *
 * @author yuhao.wang3
 */
public class StartReleaseAction extends AbstractMergeAction {

    public StartReleaseAction() {
        super("开始发布", "将当前开发分支合并到发布分支，加锁，防止再有开发分支合并到发布分支",
                IconLoader.getIcon("/icons/start.svg", Objects.requireNonNull(ReflectionUtil.getGrandCallerClass())));
    }

    @Override
    protected void setEnabledAndText(AnActionEvent event) {
        event.getPresentation().setText(I18n.getContent(I18nKey.START_RELEASE_ACTION$TEXT));
    }

    @Override
    protected String getTargetBranch(Project project) {
        return ConfigUtil.getInitOptions(project).getReleaseBranch();
    }

    @Override
    protected String getDialogTitle(Project project) {
        return I18n.getContent(I18nKey.START_RELEASE_ACTION$DIALOG_TITLE);
    }

    @Override
    protected String getTaskTitle(Project project) {
        return String.format(I18n.getContent(I18nKey.MERGE_BRANCH_TASK_TITLE), GitFlowPlus.getInstance().getCurrentBranch(project), getTargetBranch(project));
    }

    @Override
    protected List<Valve> getValves() {
        List<Valve> valves = new ArrayList<>();
        valves.add(ChangeFileValve.getInstance());
        valves.add(LockValve.getInstance());
        valves.add(MergeValve.getInstance());
        valves.add(ReleaseLockNotifyValve.getInstance());
        return valves;
    }
}
