package com.github.xiaolyuh.ui;

import com.github.xiaolyuh.utils.ExecutorUtils;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.components.JBLoadingPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import static com.github.xiaolyuh.utils.ExecutorUtils.sleep;

public class TranslateDialog {
    private JPanel mainPanel;
    private JPanel contentPanel;
    private JTextField targetTextField;
    private JTextField sourceTextField;
    private JButton replaceBtn;
    private final JBLoadingPanel loadingPanel;
    private JComboBox<String> sourceLangComboBox;
    private JComboBox<String> targetLangComboBox;
    private Consumer<String> replaceResult = val -> {
    };

    public TranslateDialog(String source) {
        setSourceLangVal(source);

        mainPanel.remove(contentPanel);

        loadingPanel = new JBLoadingPanel(new FlowLayout(), Disposer.newDisposable());
        loadingPanel.add(contentPanel);
        loadingPanel.setLoadingText("Loading......");

        mainPanel.add(loadingPanel);

        ActionListener listener = evt -> {
            translate();
        };

        sourceLangComboBox.addActionListener(listener);

        targetLangComboBox.addActionListener(listener);

        replaceBtn.addActionListener((e) -> {
            replaceResult.accept(targetTextField.getText());
        });

        translate();
    }

    private void translate() {
        loadingPanel.startLoading();
        ExecutorUtils.addTask(() -> {
            sleep(3);
            ApplicationManager.getApplication().invokeLater(() -> {
                setTargetLangVal("模拟翻译的结果");
                loadingPanel.stopLoading();
            });
        });
    }

    public void setReplaceResultListener(Consumer<String> consumer) {
        replaceResult = consumer;
    }

    private void setSourceLangVal(String val) {
        sourceTextField.setText(val);
    }

    public void setTargetLangVal(String val) {
        targetTextField.setText(val);
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

}
