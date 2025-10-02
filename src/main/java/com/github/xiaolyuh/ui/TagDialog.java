package com.github.xiaolyuh.ui;

import com.github.xiaolyuh.i18n.I18n;
import com.github.xiaolyuh.i18n.I18nKey;
import com.github.xiaolyuh.service.GitFlowPlus;
import com.github.xiaolyuh.service.GitBranchService;
import com.github.xiaolyuh.utils.StringUtils;
import com.github.xiaolyuh.vo.TagOptions;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import git4idea.repo.GitRepository;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * @author yuhao.wang3
 * @since 2020/3/27 12:15
 */
public class TagDialog extends DialogWrapper {
    private JPanel tagPanel;

    private JTextField titleTextField;
    private JTextArea messageTextArea;

    private final Project project;

    public TagDialog(@Nullable Project project) {
        super(project);
        this.project = project;
        setTitle(I18n.getContent(I18nKey.TAG_DIALOG$TITLE));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
        String text = dateFormat.format(new Date());
        titleTextField.setText(text);
        messageTextArea.setText(text);
        init();
    }

    public TagOptions getTagOptions() {
        TagOptions tagOptions = new TagOptions();
        tagOptions.setTagName(StringUtils.trim(titleTextField.getText()));
        tagOptions.setMessage(StringUtils.trim(messageTextArea.getText()));
        return tagOptions;
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        if (StringUtils.isBlank(titleTextField.getText())) {
            return new ValidationInfo(I18n.getContent(I18nKey.TAG_DIALOG$TAG_NAME_REQUIRED), titleTextField);
        }
        boolean isMatch = Pattern.matches("^[A-Za-z0-9\\-._\\u4e00-\\u9fa5]+$", titleTextField.getText());
        if (!isMatch) {
            return new ValidationInfo(I18n.getContent(I18nKey.TAG_DIALOG$TAG_NAME_ILLEGAL), titleTextField);
        }
        final GitRepository repository = GitBranchService.getCurrentRepository(project);
        if (GitFlowPlus.getInstance().isExistTag(repository, titleTextField.getText())) {
            return new ValidationInfo(I18n.getContent(I18nKey.TAG_DIALOG$TAG_NAME_EXIST), titleTextField);
        }
        if (StringUtils.isBlank(messageTextArea.getText())) {
            return new ValidationInfo(I18n.getContent(I18nKey.TAG_DIALOG$TAG_MESSAGE_REQUIRED), messageTextArea);
        }
        return null;
    }

    @Override
    protected void doOKAction() {
        super.doOKAction();
    }

    @Override
    public void doCancelAction() {
        super.doCancelAction();
    }


    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return tagPanel;
    }
}
