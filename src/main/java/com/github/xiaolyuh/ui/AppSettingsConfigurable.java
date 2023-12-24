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

    @Override
    public @Nullable JComponent createComponent() {
        configDialog = new ConfigDialog();
        return configDialog.createCenterPanel();
    }

    @Override
    public boolean isModified() {
        Pair<String, String> pair = ConfigUtil.getBaiduConfig();
        Pair<String, String> curPair = configDialog.getCurrentConfig();
        return !pair.equals(curPair);
    }

    @Override
    public void reset() {
        configDialog.resetConfig();
    }

    @Override
    public void apply() {
        configDialog.saveCurrentConfig();
    }
}
