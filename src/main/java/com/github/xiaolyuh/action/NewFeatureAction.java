package com.github.xiaolyuh.action;

import com.github.xiaolyuh.i18n.I18n;
import com.github.xiaolyuh.i18n.I18nKey;
import com.github.xiaolyuh.icons.GitFlowPlusIcons;
import com.github.xiaolyuh.model.NewBranchOption;
import com.github.xiaolyuh.service.ConfigService;
import com.github.xiaolyuh.ui.NewBranchOptionDialog;
import com.github.xiaolyuh.validator.GitNewBranchNameValidator;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import git4idea.GitUtil;
import git4idea.repo.GitRepository;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;
import java.util.List;

import static com.github.xiaolyuh.consts.Constants.DATE_PATTERN_SHORT;

/**
 * 新建开发分支
 *
 * @author yuhao.wang3
 */
public class NewFeatureAction extends AbstractNewBranchAction {

    @SuppressWarnings("ActionPresentationInstantiatedInCtor")
    public NewFeatureAction() {
        super(I18n.nls("action.feature.txt"), I18n.nls("action.feature.desc"), GitFlowPlusIcons.INSTANCE.getFeature());
    }

    @Override
    protected void setEnabledAndText(AnActionEvent event) {
        event.getPresentation().setText(I18n.getContent(I18nKey.NEW_FEATURE_ACTION$TEXT));
    }

    @Override
    public String getPrefix(Project project) {
        ConfigService configService = ConfigService.Companion.getInstance(project);
        return configService.getInitOptions().getFeaturePrefix();
    }

    @Override
    public NewBranchOption getInputString(Project project) {
        String prefix = getPrefix(project);
        List<GitRepository> repositories = GitUtil.getRepositoryManager(project).getRepositories();
        String dateStr = "_" + DateFormatUtils.format(new Date(), DATE_PATTERN_SHORT);

        String master = ConfigService.Companion.getInstance(project).getInitOptions().getMasterBranch();

        NewBranchOptionDialog dialog = new NewBranchOptionDialog(project, I18n.getContent(I18nKey.NEW_FEATURE_ACTION$DIALOG_MESSAGE),
                I18n.getContent(I18nKey.NEW_FEATURE_ACTION$DIALOG_TITLE), dateStr, master,
                GitNewBranchNameValidator.newInstance(repositories, prefix));

        if (dialog.showAndGet()) {
            return dialog.getNewBranchOption();
        }

        return null;
    }

    @Override
    public String getTitle(String branchName) {
        return I18n.getContent(I18nKey.NEW_FEATURE_ACTION$TITLE) + ": " + branchName;
    }

    @Override
    public boolean isDeleteBranch() {
        return false;
    }
}
