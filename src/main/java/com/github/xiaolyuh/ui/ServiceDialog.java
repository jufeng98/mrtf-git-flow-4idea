package com.github.xiaolyuh.ui;

import com.github.xiaolyuh.utils.ConfigUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ServiceDialog extends DialogWrapper {
    private JPanel panel1;
    @SuppressWarnings("rawtypes")
    private JList jlist;
    private JLabel jlabel;
    private static Integer lastChoose = 0;

    public ServiceDialog(String txt, Project project) {
        super(project);
        init();
        jlabel.setText(txt);

        JsonObject jsonObject = ConfigUtil.getProjectConfigFromFile(project);
        if (jsonObject != null) {
            JsonArray services = jsonObject.getAsJsonArray("services");
            DefaultListModel<String> listModel = new DefaultListModel<>();
            for (Object service : services) {
                JsonPrimitive jsonPrimitive = (JsonPrimitive) service;
                listModel.addElement(jsonPrimitive.getAsString());
            }
            //noinspection unchecked
            jlist.setModel(listModel);
        } else {
            DefaultListModel<String> listModel = new DefaultListModel<>();
            listModel.addElement("缺少服务配置,请先在 git-flow-project.json 文件配置项目服务!");
            jlist.setEnabled(false);
            //noinspection unchecked
            jlist.setModel(listModel);
        }

    }

    public String getSelectService() {
        if (jlist.getSelectedIndex() != -1) {
            lastChoose = jlist.getSelectedIndex();
        }
        if (jlist.isEnabled()) {
            return (String) jlist.getSelectedValue();
        }
        return "";
    }

    public void selectLastChoose() {
        if (!jlist.isEnabled()) {
            return;
        }
        jlist.setSelectedIndex(lastChoose);
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return panel1;
    }
}
