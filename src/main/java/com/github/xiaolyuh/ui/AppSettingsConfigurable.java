package com.github.xiaolyuh.ui;

import com.github.xiaolyuh.GitFlowPlus;
import com.github.xiaolyuh.utils.ConfigUtil;
import com.intellij.openapi.options.Configurable;
import kotlin.Pair;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static com.github.xiaolyuh.utils.ConfigUtil.PREFERENCES;

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
        PREFERENCES.put("kubesphereUsername", curPair.getFirst());
        PREFERENCES.put("kubespherePassword", curPair.getSecond());
    }
}
