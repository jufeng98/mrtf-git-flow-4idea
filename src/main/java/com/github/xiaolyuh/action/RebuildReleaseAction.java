package com.github.xiaolyuh.action;

import com.github.xiaolyuh.i18n.I18n;
import com.github.xiaolyuh.i18n.I18nKey;
import com.github.xiaolyuh.icons.GitFlowPlusIcons;
import com.github.xiaolyuh.service.ConfigService;
import com.github.xiaolyuh.utils.StringUtils;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.IconLoader;
import com.intellij.util.ReflectionUtil;

import java.util.Objects;

/**
 * 重建发布分支
 *
 * @author yuhao.wang3
 */
public class RebuildReleaseAction extends AbstractNewBranchAction {

    @SuppressWarnings("ActionPresentationInstantiatedInCtor")
    public RebuildReleaseAction() {
        super(I18n.nls("action.rebuild.release.txt"), I18n.nls("action.rebuild.release.desc"), GitFlowPlusIcons.INSTANCE.getRelease());
    }

    @Override
    protected void setEnabledAndText(AnActionEvent event) {
        event.getPresentation().setText(I18n.getContent(I18nKey.REBUILD_RELEASE_ACTION$TEXT));
        if (event.getPresentation().isEnabled()) {
            event.getPresentation().setEnabled(!gitFlowPlus.isLock(event.getProject()));
        }
    }

    @Override
    public String getPrefix(Project project) {
        return StringUtils.EMPTY;
    }

    @Override
    public String getInputString(Project project) {
        ConfigService configService = ConfigService.Companion.getInstance(project);
        String release = configService.getInitOptions().getReleaseBranch();

        int flag = Messages.showOkCancelDialog(project,
                I18n.getContent(I18nKey.REBUILD_RELEASE_ACTION$DIALOG_MESSAGE, release, release),
                I18n.getContent(I18nKey.REBUILD_RELEASE_ACTION$DIALOG_TITLE),
                I18n.getContent(I18nKey.OK_TEXT), I18n.getContent(I18nKey.CANCEL_TEXT),
                IconLoader.getIcon("/icons/warning.svg", Objects.requireNonNull(ReflectionUtil.getGrandCallerClass())));

        return flag == 0 ? release : StringUtils.EMPTY;
    }

    @Override
    public String getTitle(String branchName) {
        return I18n.getContent(I18nKey.REBUILD_RELEASE_ACTION$TITLE) + ": " + branchName;
    }
}
