package com.github.xiaolyuh.ui;

import com.github.xiaolyuh.service.ConfigService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collections;
import java.util.List;

/**
 * @author yudong
 */
public class ServiceDialog extends DialogWrapper {
    private JPanel mainPanel;
    private JList<String> jlist;
    private JLabel jlabel;
    private static Integer lastChoose = 0;

    public ServiceDialog(String txt, Project project) {
        super(project);
        init();
        jlabel.setText(txt);
        jlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultListModel<String> listModel = new DefaultListModel<>();

        ConfigService configService = ConfigService.Companion.getInstance(project);

        if (configService.existsK8sOptions()) {
            List<String> services = configService.getK8sOptions().getServices();
            for (String service : services) {
                listModel.addElement(service);
            }
        } else {
            listModel.addElement("缺少服务配置,请先在 git-flow-k8s.json 文件配置项目服务!");
            jlist.setEnabled(false);
        }

        jlist.setModel(listModel);
    }

    public String getSelectService() {
        if (jlist.getSelectedIndex() != -1) {
            lastChoose = jlist.getSelectedIndex();
        }

        if (jlist.isEnabled()) {
            return jlist.getSelectedValue();
        }

        return "";
    }

    public List<String> getSelectServices() {
        if (jlist.getSelectedIndex() != -1) {
            lastChoose = jlist.getSelectedIndex();
        }

        if (jlist.isEnabled()) {
            return jlist.getSelectedValuesList();
        }

        return Collections.emptyList();
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

    public void setSelectionMode(int selectionMode) {
        jlist.setSelectionMode(selectionMode);
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return mainPanel;
    }
}
