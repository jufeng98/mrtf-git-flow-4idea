package com.github.xiaolyuh.action;

import com.github.xiaolyuh.i18n.I18n;
import com.github.xiaolyuh.i18n.I18nKey;
import com.github.xiaolyuh.icons.GitFlowPlusIcons;
import com.github.xiaolyuh.service.ConfigService;
import com.github.xiaolyuh.ui.TagDialog;
import com.github.xiaolyuh.valve.merge.ChangeFileValve;
import com.github.xiaolyuh.valve.merge.MergeValve;
import com.github.xiaolyuh.valve.merge.UnLockCheckValve;
import com.github.xiaolyuh.valve.merge.UnLockValve;
import com.github.xiaolyuh.valve.merge.Valve;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 发布完成
 *
 * @author yuhao.wang3
 */
public class FinishReleaseAction extends AbstractMergeAction {

    @SuppressWarnings("ActionPresentationInstantiatedInCtor")
    public FinishReleaseAction() {
        super(I18n.nls("action.finish.txt"), I18n.nls("action.finish.desc"), GitFlowPlusIcons.INSTANCE.getFinished());
    }

    @Override
    protected void setEnabledAndText(AnActionEvent event) {
        event.getPresentation().setText(I18n.getContent(I18nKey.FINISH_RELEASE_ACTION$TEXT));
        if (event.getPresentation().isEnabled()) {
            event.getPresentation().setEnabled(gitFlowPlus.isLock(event.getProject()));
        }
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();

        TagDialog tagDialog = new TagDialog(project);
        tagDialog.show();

        if (!tagDialog.isOK()) {
            return;
        }

        super.actionPerformed(event, tagDialog.getTagOptions());
    }

    @Override
    protected String getDialogContent(Project project, boolean isStartTest) {
        ConfigService configService = ConfigService.Companion.getInstance(project);
        String release = configService.getInitOptions().getReleaseBranch();
        return I18n.getContent(I18nKey.MERGE_BRANCH_MSG, release, getTargetBranch(project), "");
    }

    @Override
    protected String getTargetBranch(Project project) {
        ConfigService configService = ConfigService.Companion.getInstance(project);
        return configService.getInitOptions().getMasterBranch();
    }

    @Override
    protected String getDialogTitle(Project project) {
        return I18n.getContent(I18nKey.FINISH_RELEASE_ACTION$DIALOG_TITLE);
    }

    @Override
    protected String getTaskTitle(Project project) {
        ConfigService configService = ConfigService.Companion.getInstance(project);
        String release = configService.getInitOptions().getReleaseBranch();
        return I18n.getContent(I18nKey.MERGE_BRANCH_TASK_TITLE, release, getTargetBranch(project));
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
