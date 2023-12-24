package com.github.xiaolyuh.ui;

import com.github.xiaolyuh.utils.ConfigUtil;
import com.github.xiaolyuh.utils.DigestUtils;
import com.github.xiaolyuh.utils.ExecutorUtils;
import com.github.xiaolyuh.utils.HttpClientUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.components.JBLoadingPanel;
import kotlin.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.function.Consumer;

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

    public TranslateDialog(String text) {
        setSourceLangVal(text);

        mainPanel.remove(contentPanel);
        loadingPanel = new JBLoadingPanel(new FlowLayout(), Disposer.newDisposable());
        loadingPanel.add(contentPanel);
        loadingPanel.setLoadingText("Loading......");

        mainPanel.add(loadingPanel);

        ActionListener listener = evt -> translate();

        sourceLangComboBox.addActionListener(listener);

        targetLangComboBox.addActionListener(listener);

        replaceBtn.addActionListener((e) -> replaceResult.accept(targetTextField.getText()));

        translate();
    }

    public void translate() {
        loadingPanel.startLoading();
        // 请求网络等耗时工作绝不能在UI线程进行(否则会导致IDEA界面卡住,无法响应任何操作),放到线程池执行
        ExecutorUtils.addTask(() -> {
            // 调用百度翻译api得到翻译结果
            String translateRes = translateUseBaidu();
            // 回到UI线程,因为要操作UI组件
            ApplicationManager.getApplication().invokeLater(() -> {
                setTargetLangVal(translateRes);
                loadingPanel.stopLoading();
            });
        });
    }

    private String translateUseBaidu() throws Exception {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(6))
                .build();

        String sourceText = sourceTextField.getText();
        Pair<String, String> pair = ConfigUtil.getBaiduConfig();
        String appId = pair.getFirst();
        String appKey = pair.getSecond();
        long salt = System.currentTimeMillis();
        String s = appId + sourceText + salt + appKey;
        String sign = DigestUtils.md5DigestAsHex(s.getBytes(StandardCharsets.UTF_8));

        String query = String.format("q=%s&from=%s&to=%s&appid=%s&salt=%s&sign=%s", sourceText, "en", "zh",
                appId, salt, sign);

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(query, StandardCharsets.UTF_8))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .uri(URI.create("http://api.fanyi.baidu.com/api/trans/vip/translate"));

        HttpRequest request = builder.build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String body = response.body();
        JsonObject resObj = HttpClientUtil.gson.fromJson(body, JsonObject.class);
        JsonElement errorMsgEle = resObj.get("error_msg");
        if (errorMsgEle != null) {
            String errorMsg = errorMsgEle.getAsString();
            throw new RuntimeException(errorMsg);
        }
        JsonArray transResult = resObj.getAsJsonArray("trans_result");
        JsonObject res = (JsonObject) transResult.get(0);
        return res.get("dst").getAsString();
    }

    public void addReplaceResultListener(Consumer<String> consumer) {
        replaceResult = consumer;
    }

    private void setSourceLangVal(String val) {
        sourceTextField.setText(val);
    }

    public void setTargetLangVal(String val) {
        targetTextField.setText(val);
    }

    public JPanel createCenterPanel() {
        return mainPanel;
    }

}
