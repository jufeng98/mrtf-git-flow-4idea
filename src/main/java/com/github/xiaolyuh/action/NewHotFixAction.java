package com.github.xiaolyuh.action;

import com.github.xiaolyuh.i18n.I18n;
import com.github.xiaolyuh.i18n.I18nKey;
import com.github.xiaolyuh.service.ConfigService;
import com.github.xiaolyuh.validator.GitNewBranchNameValidator;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import git4idea.GitUtil;
import git4idea.repo.GitRepository;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;
import java.util.List;

import static com.github.xiaolyuh.consts.Constants.DATE_PATTERN_SHORT;

/**
 * 新建修复分支
 *
 * @author yuhao.wang3
 */
public class NewHotFixAction extends AbstractNewBranchAction {

    @Override
    protected void setEnabledAndText(AnActionEvent event) {
        event.getPresentation().setText(I18n.getContent(I18nKey.NEW_HOT_FIX_ACTION$TEXT));
    }

    @Override
    public String getPrefix(Project project) {
        ConfigService configService = ConfigService.Companion.getInstance(project);
        return configService.getInitOptions().getHotfixPrefix();
    }

    @Override
    public String getInputString(Project project) {
        String prefix = getPrefix(project);
        List<GitRepository> repositories = GitUtil.getRepositoryManager(project).getRepositories();
        String dateStr = "_" + DateFormatUtils.format(new Date(), DATE_PATTERN_SHORT);

        return Messages.showInputDialog(project, I18n.getContent(I18nKey.NEW_HOT_FIX_ACTION$DIALOG_MESSAGE),
                I18n.getContent(I18nKey.NEW_HOT_FIX_ACTION$DIALOG_TITLE), null, dateStr,
                GitNewBranchNameValidator.newInstance(repositories, prefix));
    }

    @Override
    public String getTitle(String branchName) {
        return I18n.getContent(I18nKey.NEW_HOT_FIX_ACTION$TITLE) + ": " + branchName;
    }


    @Override
    public boolean isDeleteBranch() {
        return false;
    }
}
