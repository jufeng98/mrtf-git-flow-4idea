package com.github.xiaolyuh.action;

import com.github.xiaolyuh.i18n.I18n;
import com.github.xiaolyuh.i18n.I18nKey;
import com.github.xiaolyuh.icons.GitFlowPlusIcons;
import com.github.xiaolyuh.service.ConfigService;
import com.github.xiaolyuh.service.GitFlowPlus;
import com.github.xiaolyuh.utils.ActionUtils;
import com.github.xiaolyuh.valve.merge.*;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;

import java.util.ArrayList;
import java.util.List;

/**
 * 开始发布
 *
 * @author yuhao.wang3
 */
public class StartReleaseAction extends AbstractMergeAction {

    @SuppressWarnings("ActionPresentationInstantiatedInCtor")
    public StartReleaseAction() {
        super(I18n.nls("action.release.txt"), I18n.nls("action.release.desc"), GitFlowPlusIcons.INSTANCE.getStart());
    }

    @Override
    protected void setEnabledAndText(AnActionEvent event) {
        Presentation presentation = event.getPresentation();
        presentation.setText(I18n.getContent(I18nKey.START_RELEASE_ACTION$TEXT));

        if (!presentation.isEnabled()) {
            presentation.setEnabled(ActionUtils.INSTANCE.isStagingBranch(event));
        }
    }

    @Override
    protected String getTargetBranch(Project project) {
        ConfigService configService = ConfigService.Companion.getInstance(project);
        return configService.getInitOptions().getReleaseBranch();
    }

    @Override
    protected String getDialogTitle(Project project) {
        return I18n.getContent(I18nKey.START_RELEASE_ACTION$DIALOG_TITLE);
    }

    @Override
    protected String getTaskTitle(Project project) {
        return I18n.getContent(I18nKey.MERGE_BRANCH_TASK_TITLE,
                GitFlowPlus.getInstance().getCurrentBranch(project), getTargetBranch(project));
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
