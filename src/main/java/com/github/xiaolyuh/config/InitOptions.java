package com.github.xiaolyuh.config;

import com.github.xiaolyuh.enums.LanguageEnum;

import java.util.Objects;

/**
 * 初始化参数
 *
 * @author yuhao.wang3
 */
public class InitOptions {

    /**
     * 主干分支名称
     */
    private String masterBranch;

    /**
     * 发布分支名称
     */
    private String releaseBranch;

    /**
     * 测试分支名称
     */
    private String testBranch;

    private String testBranchSec;

    /**
     * 开发分支前缀
     */
    private String featurePrefix;

    /**
     * 修复分支前缀
     */
    private String hotfixPrefix;

    /**
     * 版本前缀
     */
    private String tagPrefix;

    /**
     * 发布完成是否删除发布分支
     */
    private boolean releaseFinishIsDeleteRelease;

    /**
     * 发布完成是否删除开发分支
     */
    private boolean releaseFinishIsDeleteFeature;

    /**
     * 钉钉Token
     */
    private String dingtalkToken;
    private transient String kubesphereUsername;
    private transient String kubespherePassword;

    public String getFsWebHookUrl() {
        return fsWebHookUrl;
    }

    public void setFsWebHookUrl(String fsWebHookUrl) {
        this.fsWebHookUrl = fsWebHookUrl;
    }

    private transient String fsWebHookUrl;

    /**
     * 语言
     */
    private LanguageEnum language;

    public String getMasterBranch() {
        return masterBranch;
    }

    public void setMasterBranch(String masterBranch) {
        this.masterBranch = masterBranch;
    }

    public String getReleaseBranch() {
        return releaseBranch;
    }

    public void setReleaseBranch(String releaseBranch) {
        this.releaseBranch = releaseBranch;
    }

    public String getTestBranch() {
        return testBranch;
    }

    public void setTestBranch(String testBranch) {
        this.testBranch = testBranch;
    }

    public String getTestBranchSec() {
        return testBranchSec;
    }

    public void setTestBranchSec(String testBranchSec) {
        this.testBranchSec = testBranchSec;
    }

    public String getFeaturePrefix() {
        return featurePrefix;
    }

    public void setFeaturePrefix(String featurePrefix) {
        this.featurePrefix = featurePrefix;
    }

    public String getHotfixPrefix() {
        return hotfixPrefix;
    }

    public void setHotfixPrefix(String hotfixPrefix) {
        this.hotfixPrefix = hotfixPrefix;
    }

    public String getTagPrefix() {
        return tagPrefix;
    }

    public void setTagPrefix(String tagPrefix) {
        this.tagPrefix = tagPrefix;
    }

    public boolean isReleaseFinishIsDeleteRelease() {
        return releaseFinishIsDeleteRelease;
    }

    public void setReleaseFinishIsDeleteRelease(boolean releaseFinishIsDeleteRelease) {
        this.releaseFinishIsDeleteRelease = releaseFinishIsDeleteRelease;
    }

    public boolean isReleaseFinishIsDeleteFeature() {
        return releaseFinishIsDeleteFeature;
    }

    public void setReleaseFinishIsDeleteFeature(boolean releaseFinishIsDeleteFeature) {
        this.releaseFinishIsDeleteFeature = releaseFinishIsDeleteFeature;
    }

    public String getDingtalkToken() {
        return dingtalkToken;
    }

    public void setDingtalkToken(String dingtalkToken) {
        this.dingtalkToken = dingtalkToken;
    }

    public String getKubesphereUsername() {
        return kubesphereUsername;
    }

    public void setKubesphereUsername(String kubesphereUsername) {
        this.kubesphereUsername = kubesphereUsername;
    }

    public String getKubespherePassword() {
        return kubespherePassword;
    }

    public void setKubespherePassword(String kubespherePassword) {
        this.kubespherePassword = kubespherePassword;
    }

    public LanguageEnum getLanguage() {
        return Objects.isNull(language) ? LanguageEnum.CN : language;
    }

    public void setLanguage(LanguageEnum language) {
        this.language = language;
    }
}
