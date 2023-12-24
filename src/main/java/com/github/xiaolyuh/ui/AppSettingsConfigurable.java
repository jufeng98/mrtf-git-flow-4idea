package com.github.xiaolyuh.ui;

import com.github.xiaolyuh.service.GitFlowPlus;
import com.github.xiaolyuh.utils.ConfigUtil;
import com.intellij.openapi.options.Configurable;
import kotlin.Pair;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class AppSettingsConfigurable implements Configurable {
    private ConfigDialog configDialog;

    @Override
    public String getDisplayName() {
        return GitFlowPlus.class.getName();
    }

    /**
     * 创建用于显示设置的界面
     */
    @Override
    public @Nullable JComponent createComponent() {
        configDialog = new ConfigDialog();
        return configDialog.createCenterPanel();
    }

    /**
     * 判断当前页面配置是否发生了变化
     */
    @Override
    public boolean isModified() {
        Pair<String, String> pair = ConfigUtil.getBaiduConfig();
        Pair<String, String> curPair = configDialog.getCurrentConfig();
        return !pair.equals(curPair);
    }

    /**
     * 重置配置
     */
    @Override
    public void reset() {
        configDialog.resetConfig();
    }

    /**
     * 保存配置
     */
    @Override
    public void apply() {
        configDialog.saveCurrentConfig();
    }
}
