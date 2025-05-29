package com.github.xiaolyuh.action;

import com.github.xiaolyuh.i18n.I18n;
import com.github.xiaolyuh.i18n.I18nKey;
import com.github.xiaolyuh.icons.GitFlowPlusIcons;
import com.github.xiaolyuh.service.ConfigService;
import com.github.xiaolyuh.valve.merge.ChangeFileValve;
import com.github.xiaolyuh.valve.merge.UnLockCheckValve;
import com.github.xiaolyuh.valve.merge.UnLockValve;
import com.github.xiaolyuh.valve.merge.Valve;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;

import java.util.ArrayList;
import java.util.List;

/**
 * 发布失败
 *
 * @author yuhao.wang3
 */
public class FailureReleaseAction extends AbstractMergeAction {

    @SuppressWarnings("ActionPresentationInstantiatedInCtor")
    public FailureReleaseAction() {
        super(I18n.nls("action.failure.txt"), I18n.nls("action.failure.desc"), GitFlowPlusIcons.INSTANCE.getFailure());
    }

    @Override
    protected void setEnabledAndText(AnActionEvent event) {
        event.getPresentation().setText(I18n.getContent(I18nKey.FAILURE_RELEASE_ACTION$TEXT));
        if (event.getPresentation().isEnabled()) {
            event.getPresentation().setEnabled(gitFlowPlus.isLock(event.getProject()));
        }
    }

    @Override
    protected String getTargetBranch(Project project) {
        ConfigService configService = ConfigService.Companion.getInstance(project);
        return configService.getInitOptions().getMasterBranch();
    }

    @Override
    protected String getDialogTitle(Project project) {
        return I18n.getContent(I18nKey.FAILURE_RELEASE_ACTION$DIALOG_TITLE);
    }

    @Override
    protected String getDialogContent(Project project, boolean isStartTest) {
        ConfigService configService = ConfigService.Companion.getInstance(project);
        String release = configService.getInitOptions().getReleaseBranch();
        return I18n.getContent(I18nKey.FAILURE_RELEASE_ACTION$DIALOG_CONTENT, release);
    }

    @Override
    protected String getTaskTitle(Project project) {
        ConfigService configService = ConfigService.Companion.getInstance(project);
        String release = configService.getInitOptions().getReleaseBranch();
        return I18n.getContent(I18nKey.FAILURE_RELEASE_ACTION$TASK_TITLE, release);
    }

    @Override
    protected List<Valve> getValves() {
        List<Valve> valves = new ArrayList<>();
        valves.add(ChangeFileValve.getInstance());
        valves.add(UnLockCheckValve.getInstance());
        valves.add(UnLockValve.getInstance());
        return valves;
    }
}
