package com.github.xiaolyuh.http.ui;

import javax.swing.*;

public class HttpEditorTopForm extends JComponent {
    private JComboBox<String> envComboBox;
    public JPanel mainPanel;

    public HttpEditorTopForm() {
        envComboBox.addItem("dev");
        envComboBox.addItem("uat");
        envComboBox.addItem("pro");
        envComboBox.setToolTipText("读取当前http文件所在的如 env-dev.json 构建环境");
    }

}
