package com.github.xiaolyuh.action;

import com.github.xiaolyuh.i18n.I18n;
import com.github.xiaolyuh.i18n.I18nKey;
import com.github.xiaolyuh.icons.GitFlowPlusIcons;
import com.github.xiaolyuh.service.ConfigService;
import com.github.xiaolyuh.service.GitFlowPlus;
import com.github.xiaolyuh.utils.ActionUtils;
import com.github.xiaolyuh.valve.merge.ChangeFileValve;
import com.github.xiaolyuh.valve.merge.MergeValve;
import com.github.xiaolyuh.valve.merge.Valve;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class StartTestSecAction extends AbstractMergeAction {

    @SuppressWarnings("ActionPresentationInstantiatedInCtor")
    public StartTestSecAction() {
        super(I18n.nls("action.test.txt") + "(Sec)", I18n.nls("action.test.desc") + "(Sec)", GitFlowPlusIcons.INSTANCE.getMergeToTest());
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        boolean showSec = ActionUtils.INSTANCE.shouldShowStartTestSec(event);

        event.getPresentation().setEnabledAndVisible(showSec);
    }

    @Override
    protected void setEnabledAndText(AnActionEvent event) {
    }

    @Override
    protected String getTargetBranch(Project project) {
        ConfigService configService = ConfigService.Companion.getInstance(project);
        return configService.getInitOptions().getTestBranchSec();
    }

    @Override
    protected String getDialogTitle(Project project) {
        return I18nKey.START_TEST_ACTION$DIALOG_TITLE;
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
        valves.add(MergeValve.getInstance());
        return valves;
    }
}



