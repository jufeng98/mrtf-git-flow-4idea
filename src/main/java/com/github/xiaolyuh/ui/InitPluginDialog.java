package com.github.xiaolyuh.ui;

import com.github.xiaolyuh.config.InitOptions;
import com.github.xiaolyuh.enums.LanguageEnum;
import com.github.xiaolyuh.i18n.*;
import com.github.xiaolyuh.service.ConfigService;
import com.github.xiaolyuh.service.GitBranchService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.CollectionComboBoxModel;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;
import java.util.Objects;

/**
 * 初始化插件弹框
 *
 * @author yuhao.wang3
 */
public class InitPluginDialog extends DialogWrapper {
    private JPanel contentPane;

    private JComboBox<String> masterBranchComboBox;
    private JComboBox<String> releaseBranchComboBox;
    private JComboBox<String> testBranchComboBox;
    private JComboBox<String> testBranchComboBoxSec;
    private JTextField featurePrefixTextField;
    private JTextField hotfixPrefixTextField;
    private JTextField tagPrefixTextField;
    private JTextField dingtalkTokenTextField;
    private JTextField kubesphereUsernameTextField;
    private JPasswordField kubespherePasswordTextField;
    private JCheckBox releaseFinishIsDeleteReleaseCheckBox;
    private JCheckBox releaseFinishIsDeleteFeatureCheckBox;
    private JCheckBox needTagCheckBox;
    private JLabel langLabel;
    private JComboBox<String> languageComboBox;
    private JLabel specialBranchConfigLabel;
    private JLabel mastBranchLabel;
    private JLabel releaseBranchLabel;
    private JLabel testBranchLabel;
    private JLabel testBranchLabelSec;
    private JLabel branchOptionsConfig;
    private JLabel branchPrefixConfigLabel;
    private JLabel featureBranchPrefixLabel;
    private JLabel hotfixBranchPrefixLabel;
    private JLabel tagNamePrefixLabel;
    private JPanel mainPanel;

    public InitPluginDialog(Project project) {
        super(project);

        setTitle(I18n.getContent(I18nKey.INIT_PLUGIN_DIALOG$TITLE));

        initDialog(project);

        init();

        langLabel.setVisible(false);
        languageComboBox.setVisible(false);
        languageComboBox.addItemListener(e -> languageSwitch(LanguageEnum.getByLanguage((String) languageComboBox.getSelectedItem())));

        mainPanel.remove(langLabel);
        mainPanel.remove(languageComboBox);
        mainPanel.remove(releaseFinishIsDeleteFeatureCheckBox);
        mainPanel.remove(releaseFinishIsDeleteReleaseCheckBox);
    }

    private void languageSwitch(LanguageEnum language) {
        setTitle(I18n.getContent(I18nKey.INIT_PLUGIN_DIALOG$TITLE, language));

        specialBranchConfigLabel.setText(I18n.getContent(I18nKey.INIT_PLUGIN_DIALOG$SPECIAL_BRANCH_CONFIG_LABEL, language));
        mastBranchLabel.setText(I18n.getContent(I18nKey.INIT_PLUGIN_DIALOG$MAST_BRANCH_LABEL, language));
        releaseBranchLabel.setText(I18n.getContent(I18nKey.INIT_PLUGIN_DIALOG$RELEASE_BRANCH_LABEL, language));
        testBranchLabel.setText(I18n.getContent(I18nKey.INIT_PLUGIN_DIALOG$TEST_BRANCH_LABEL, language));
        testBranchLabelSec.setText(I18n.getContent(I18nKey.INIT_PLUGIN_DIALOG$TEST_BRANCH_LABEL2, language));
        branchOptionsConfig.setText(I18n.getContent(I18nKey.INIT_PLUGIN_DIALOG$BRANCH_OPTIONS_CONFIG, language));
        releaseFinishIsDeleteReleaseCheckBox.setText(I18n.getContent(I18nKey.INIT_PLUGIN_DIALOG$RELEASE_FINISH_DELETE_RELEASE, language));
        releaseFinishIsDeleteFeatureCheckBox.setText(I18n.getContent(I18nKey.INIT_PLUGIN_DIALOG$RELEASE_FINISH_DELETE_FEATURE, language));
        needTagCheckBox.setText(I18n.getContent("need.tag", language));
        branchPrefixConfigLabel.setText(I18n.getContent(I18nKey.INIT_PLUGIN_DIALOG$BRANCH_PREFIX_CONFIG_LABEL, language));
        featureBranchPrefixLabel.setText(I18n.getContent(I18nKey.INIT_PLUGIN_DIALOG$FEATURE_BRANCH_PREFIX_LABEL, language));
        hotfixBranchPrefixLabel.setText(I18n.getContent(I18nKey.INIT_PLUGIN_DIALOG$HOTFIX_BRANCH_PREFIX_LABEL, language));
        tagNamePrefixLabel.setText(I18n.getContent(I18nKey.INIT_PLUGIN_DIALOG$TAG_NAME_PREFIX_LABEL, language));
    }


    public InitOptions getOptions() {
        InitOptions options = new InitOptions();

        options.setMasterBranch((String) masterBranchComboBox.getSelectedItem());
        options.setReleaseBranch((String) releaseBranchComboBox.getSelectedItem());
        options.setTestBranch((String) testBranchComboBox.getSelectedItem());
        options.setTestBranchSec((String) testBranchComboBoxSec.getSelectedItem());
        options.setFeaturePrefix(featurePrefixTextField.getText());
        options.setHotfixPrefix(hotfixPrefixTextField.getText());
        options.setTagPrefix(tagPrefixTextField.getText());
        options.setReleaseFinishIsDeleteFeature(releaseFinishIsDeleteFeatureCheckBox.isSelected());
        options.setReleaseFinishIsDeleteRelease(releaseFinishIsDeleteReleaseCheckBox.isSelected());
        options.setNeedTag(needTagCheckBox.isSelected());
        options.setDingtalkToken(dingtalkTokenTextField.getText());
        options.setKubesphereUsername(kubesphereUsernameTextField.getText());
        options.setKubespherePassword(String.valueOf(kubespherePasswordTextField.getPassword()));
        options.setLanguage(LanguageEnum.getByLanguage((String) languageComboBox.getSelectedItem()));

        return options;
    }

    /**
     * 初始化弹框
     */
    private void initDialog(Project project) {
        ConfigService configService = ConfigService.Companion.getInstance(project);

        InitOptions options = configService.getInitOptionsNullable();
        List<String> remoteBranches = GitBranchService.getRemoteBranches(project);
        List<String> languages = LanguageEnum.getAllLanguage();

        if (options != null) {
            masterBranchComboBox.setModel(new CollectionComboBoxModel<>(remoteBranches, options.getMasterBranch()));
            releaseBranchComboBox.setModel(new CollectionComboBoxModel<>(remoteBranches, options.getReleaseBranch()));
            testBranchComboBox.setModel(new CollectionComboBoxModel<>(remoteBranches, options.getTestBranch()));
            testBranchComboBoxSec.setModel(new CollectionComboBoxModel<>(remoteBranches, options.getTestBranchSec()));
            languageComboBox.setModel(new CollectionComboBoxModel<>(languages, options.getLanguage().getLanguage()));

            featurePrefixTextField.setText(options.getFeaturePrefix());
            hotfixPrefixTextField.setText(options.getHotfixPrefix());
            tagPrefixTextField.setText(options.getTagPrefix());
            releaseFinishIsDeleteReleaseCheckBox.setSelected(false);
            releaseFinishIsDeleteFeatureCheckBox.setSelected(false);
            needTagCheckBox.setSelected(options.isNeedTag());
            dingtalkTokenTextField.setText(options.getDingtalkToken());
            kubesphereUsernameTextField.setText(options.getKubesphereUsername());
            kubespherePasswordTextField.setText(options.getKubespherePassword());
        } else {
            masterBranchComboBox.setModel(new CollectionComboBoxModel<>(remoteBranches));
            releaseBranchComboBox.setModel(new CollectionComboBoxModel<>(remoteBranches));
            testBranchComboBox.setModel(new CollectionComboBoxModel<>(remoteBranches));
            testBranchComboBoxSec.setModel(new CollectionComboBoxModel<>(remoteBranches));
            languageComboBox.setModel(new CollectionComboBoxModel<>(languages, UiBundle.INSTANCE.getLanguageEnum().getLanguage()));
        }

        languageSwitch(LanguageEnum.getByLanguage((String) languageComboBox.getSelectedItem()));
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
    protected ValidationInfo doValidate() {
        if (Objects.equals(masterBranchComboBox.getSelectedItem(), releaseBranchComboBox.getSelectedItem())) {
            return new ValidationInfo(I18n.getContent(I18nKey.INIT_PLUGIN_DIALOG$RELEASE_LIKE_MASTER), releaseBranchComboBox);
        }

        Object testBranch = testBranchComboBox.getSelectedItem();
        if (Objects.equals(masterBranchComboBox.getSelectedItem(), testBranch)) {
            return new ValidationInfo(I18n.getContent(I18nKey.INIT_PLUGIN_DIALOG$TEST_LIKE_MASTER), testBranchComboBox);
        }
        if (Objects.equals(releaseBranchComboBox.getSelectedItem(), testBranch)) {
            return new ValidationInfo(I18n.getContent(I18nKey.INIT_PLUGIN_DIALOG$TEST_LIKE_RELEASE), testBranchComboBox);
        }

        Object testBranchSec = testBranchComboBoxSec.getSelectedItem();
        if (Objects.equals(masterBranchComboBox.getSelectedItem(), testBranchSec)) {
            return new ValidationInfo(I18n.getContent(I18nKey.INIT_PLUGIN_DIALOG$TEST_LIKE_MASTER), testBranchComboBox);
        }
        if (Objects.equals(releaseBranchComboBox.getSelectedItem(), testBranchSec)) {
            return new ValidationInfo(I18n.getContent(I18nKey.INIT_PLUGIN_DIALOG$TEST_LIKE_RELEASE), testBranchComboBox);
        }

        if (StringUtil.isEmptyOrSpaces(featurePrefixTextField.getText())) {
            return new ValidationInfo(I18n.getContent(I18nKey.INIT_PLUGIN_DIALOG$FEATURE_PREFIX_REQUIRED), featurePrefixTextField);
        }
        if (StringUtil.isEmptyOrSpaces(hotfixPrefixTextField.getText())) {
            return new ValidationInfo(I18n.getContent(I18nKey.INIT_PLUGIN_DIALOG$HOTFIX_PREFIX_REQUIRED), hotfixPrefixTextField);
        }
        return null;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return contentPane;
    }
}
