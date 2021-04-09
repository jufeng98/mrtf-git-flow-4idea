package com.github.xiaolyuh.ui;

import com.github.xiaolyuh.MergeRequestOptions;
import com.github.xiaolyuh.i18n.I18n;
import com.github.xiaolyuh.i18n.I18nKey;
import com.github.xiaolyuh.utils.StringUtils;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author yuhao.wang3
 * @since 2020/3/27 12:15
 */
public class MergeRequestDialog extends DialogWrapper {
    private JPanel tagPanel;

    private JTextField titleTextField;
    private JTextArea messageTextArea;

    private Project project;

    public MergeRequestDialog(@Nullable Project project) {
        super(project);
        this.project = project;
        setTitle(I18n.getContent(I18nKey.MERGE_REQUEST_DIALOG$TITLE));
        init();
    }

    public MergeRequestOptions getMergeRequestOptions() {
        MergeRequestOptions tagOptions = new MergeRequestOptions();
        tagOptions.setTitle(StringUtils.trim(titleTextField.getText()));
        tagOptions.setMessage(StringUtils.trim(messageTextArea.getText()));
        return tagOptions;
    }

    @javax.annotation.Nullable
    @Override
    protected ValidationInfo doValidate() {
        if (StringUtils.isBlank(titleTextField.getText())) {
            return new ValidationInfo(I18n.getContent(I18nKey.MERGE_REQUEST_DIALOG$TITLE_REQUIRED), titleTextField);
        }
        if (StringUtils.isBlank(messageTextArea.getText())) {
            return new ValidationInfo(I18n.getContent(I18nKey.MERGE_REQUEST_DIALOG$MESSAGE_REQUIRED), messageTextArea);
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
