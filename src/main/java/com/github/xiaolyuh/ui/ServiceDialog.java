package com.github.xiaolyuh.ui;

import com.github.xiaolyuh.utils.ConfigUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

public class ServiceDialog extends DialogWrapper {
    private JPanel mainPanel;
    @SuppressWarnings("rawtypes")
    private JList jlist;
    private JLabel jlabel;
    private static Integer lastChoose = 0;

    public ServiceDialog(String txt, Project project) {
        super(project);
        init();
        jlabel.setText(txt);

        if (ConfigUtil.existsK8sOptions(project)) {
            List<String> services = ConfigUtil.getK8sOptions(project).getServices();
            DefaultListModel<String> listModel = new DefaultListModel<>();
            for (String service : services) {
                listModel.addElement(service);
            }
            //noinspection unchecked
            jlist.setModel(listModel);
        } else {
            DefaultListModel<String> listModel = new DefaultListModel<>();
            listModel.addElement("缺少服务配置,请先在 git-flow-k8s.json 文件配置项目服务!");
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
        if (lastChoose >= jlist.getModel().getSize()) {
            lastChoose = 0;
        }
        jlist.setSelectedIndex(lastChoose);
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return mainPanel;
    }
}
