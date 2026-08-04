package com.github.xiaolyuh.ui;

import com.github.xiaolyuh.model.NewBranchOption;
import com.github.xiaolyuh.service.GitBranchService;
import com.github.xiaolyuh.utils.StringUtils;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.*;
import com.intellij.ui.CollectionComboBoxModel;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;
import java.util.Objects;

public class NewBranchOptionDialog extends DialogWrapper {
    private final InputValidatorEx validator;
    private JPanel contentPane;
    private JLabel descLabel;
    private JTextField textField;
    private JComboBox<String> comboBox;

    public NewBranchOptionDialog(Project project, String desc, String title, String def, String baseBranchName,
                                 InputValidatorEx validator) {
        super(project);

        this.validator = validator;
        setTitle(title);
        setModal(true);
        descLabel.setText(desc);
        textField.setText(def);

        List<String> remoteBranches = GitBranchService.getRemoteBranches(project);
        comboBox.setModel(new CollectionComboBoxModel<>(remoteBranches, baseBranchName));

        init();
    }

    public NewBranchOption getNewBranchOption() {
        return new NewBranchOption(textField.getText(), (String) Objects.requireNonNull(comboBox.getSelectedItem()));
    }

    @Override
    protected @Nullable ValidationInfo doValidate() {
        String name = textField.getText();
        boolean checked = validator.checkInput(name);
        if (!checked) {
            return new ValidationInfo(Objects.requireNonNull(validator.getErrorText(name)), textField);
        }

        String baseBranchName = (String) comboBox.getSelectedItem();
        if (StringUtils.isBlank(baseBranchName)) {
            return new ValidationInfo("Can't blank", comboBox);
        }

        return super.doValidate();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return contentPane;
    }
}
