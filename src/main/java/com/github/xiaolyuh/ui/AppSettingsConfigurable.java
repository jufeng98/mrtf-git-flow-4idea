package com.github.xiaolyuh.ui;

import com.github.xiaolyuh.service.GitFlowPlus;
import com.github.xiaolyuh.utils.ConfigUtil;
import com.intellij.openapi.options.Configurable;
import kotlin.Pair;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class AppSettingsConfigurable implements Configurable {
    private KubesphereDialog kubesphereDialog;

    @Override
    public String getDisplayName() {
        return GitFlowPlus.class.getName();
    }

    @Override
    public @Nullable JComponent createComponent() {
        kubesphereDialog = new KubesphereDialog();
        return kubesphereDialog.getMainPanel();
    }

    @Override
    public boolean isModified() {
        Pair<String, String> pair = ConfigUtil.getKubesphereUser();
        Pair<String, String> curPair = kubesphereDialog.getCurKubesphereUser();
        return !pair.equals(curPair);
    }

    @Override
    public void reset() {
        kubesphereDialog.fillCurKubesphereUser();
    }

    @Override
    public void apply() {
        Pair<String, String> curPair = kubesphereDialog.getCurKubesphereUser();
        ConfigUtil.saveKubesphereUser(curPair.getFirst(), curPair.getSecond());
    }
}
