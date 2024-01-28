package com.github.xiaolyuh.ui;

import com.github.xiaolyuh.config.K8sOptions;
import com.github.xiaolyuh.utils.ConfigUtil;
import com.github.xiaolyuh.utils.ExecutorUtils;
import com.github.xiaolyuh.utils.NotifyUtil;
import com.github.xiaolyuh.vo.InstanceVo;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.Pair;
import com.intellij.ui.jcef.JBCefApp;
import com.intellij.ui.jcef.JBCefBrowser;
import com.intellij.ui.jcef.JBCefBrowserBuilder;
import com.intellij.ui.jcef.JBCefClient;
import org.cef.CefApp;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefAppHandlerAdapter;
import org.cef.handler.CefLoadHandlerAdapter;
import org.cef.network.CefCookie;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.im.InputContext;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class JcefK8sConsoleDialog extends DialogWrapper {
    private JPanel contentPanel;
    private JPanel mainPanel;
    private JButton logBtn;
    private JButton debugBtn;
    private JButton errorBtn;
    private JButton infoBtn;
    private JButton closeBtn;
    private JButton watchBtn;
    private JButton copyBtn;
    private JButton pasteBtn;

    public JcefK8sConsoleDialog(InstanceVo instanceVo, @Nullable Project project, String selectService) {
        super(project);
        setModal(false);
        selectService = selectService.toLowerCase();
        setTitle(instanceVo.getDesc() + ":" + instanceVo.getName());
        init();

        String url = ConfigUtil.getConsoleUrl(project, selectService, instanceVo.getName());

        String finalSelectService = selectService;
        initJcef(url, jbCefBrowser -> {
            if (jbCefBrowser == null) {
                NotifyUtil.notifySuccess(project, "JCEF已完成初始化,重新打开菜单即可");
                return;
            }

            CefBrowser cefBrowser = jbCefBrowser.getCefBrowser();

            Pair<Pair<String, String>, Pair<String, String>> pair = getLogPath(project, finalSelectService);
            String logDir = pair.getFirst().getFirst();

            copyBtn.addActionListener(e -> {
                cefBrowser.getMainFrame().copy();
                cefBrowser.setFocus(true);
            });
            pasteBtn.addActionListener(e -> {
                cefBrowser.getMainFrame().paste();
                cefBrowser.setFocus(true);
            });

            watchBtn.addActionListener(e -> {
                sendKeyEvents("cd " + logDir + "\n", cefBrowser);
                sendKeyEvents("ls" + "\n", cefBrowser);
                sendKeyEvents("tail -f -n 600 \t", cefBrowser);
            });
            logBtn.addActionListener(e -> sendKeyEvents("cd " + logDir + "\n", cefBrowser));

            debugBtn.addActionListener(e -> executeCommand("less " + logDir + "/" + pair.getFirst().getSecond() + "\n", cefBrowser, url));
            errorBtn.addActionListener(e -> executeCommand("less " + logDir + "/" + pair.getSecond().getFirst() + "\n", cefBrowser, url));
            infoBtn.addActionListener(e -> executeCommand("less " + logDir + "/" + pair.getSecond().getSecond() + "\n", cefBrowser, url));

            closeBtn.addActionListener(e -> JcefK8sConsoleDialog.this.close(CLOSE_EXIT_CODE, true));

            contentPanel.add(jbCefBrowser.getComponent(), BorderLayout.CENTER);

            show();
        });
    }

    private void sendKeyEvents(String string, CefBrowser cefBrowser) {
        char[] charArray = string.toCharArray();
        KeyEvent keyEvent = new KeyEvent(contentPanel.getComponent(0), KeyEvent.KEY_TYPED,
                10, 0, KeyEvent.VK_UNDEFINED, 'A');
        for (char c : charArray) {
            keyEvent.setKeyChar(c);
            cefBrowser.sendKeyEvent(keyEvent);
        }
        cefBrowser.setFocus(true);
    }

    private void initJcef(String url, Consumer<JBCefBrowser> consumer) {
        JBCefApp jbCefApp = JBCefApp.getInstance();
        JBCefClient[] jbCefClient = new JBCefClient[1];

        if (CefApp.getState() == CefApp.CefAppState.INITIALIZED) {
            jbCefClient[0] = jbCefApp.createClient();
            JBCefBrowser jbCefBrowser = initJBCefBrowser(url, jbCefClient[0]);
            consumer.accept(jbCefBrowser);
            return;
        }

        CefApp.addAppHandler(new CefAppHandlerAdapter(null) {
            @Override
            public void stateHasChanged(CefApp.CefAppState state) {
                if (state != CefApp.CefAppState.INITIALIZED) {
                    return;
                }
                consumer.accept(null);
            }
        });

        jbCefClient[0] = jbCefApp.createClient();
    }

    private JBCefBrowser initJBCefBrowser(String url, JBCefClient jbCefClient) {
        JBCefBrowserBuilder jbCefBrowserBuilder = new JBCefBrowserBuilder()
                .setClient(jbCefClient)
                .setUrl(url)
                .setCreateImmediately(true);
        JBCefBrowser jbCefBrowser = JBCefBrowser.create(jbCefBrowserBuilder);


        CefBrowser cefBrowser = jbCefBrowser.getCefBrowser();

        setTokenCookie(jbCefBrowser, url);

        jbCefClient.addLoadHandler(new CefLoadHandlerAdapter() {

            @Override
            public void onLoadEnd(CefBrowser browser, CefFrame frame, int httpStatusCode) {
                browser.executeJavaScript(interceptWs(), url, 1);
                ExecutorUtils.addTask(() -> {
                    while (browser.isLoading()) {
                        TimeUnit.MILLISECONDS.sleep(100);
                    }
                    browser.executeJavaScript("document.getElementsByClassName('_1jhjCgxv3S5BKvppnuJpUJ')[0].style.display='none';", url, 1);
                    browser.executeJavaScript("document.getElementsByClassName('_2s7aowlSdhOYMXS2Jm7vG6')[0].style.display='none';", url, 1);
                    browser.executeJavaScript("document.getElementsByClassName('_1L_vmkiDzIokO_PY6QgCyV')[0].style.top='5px';", url, 1);
                    browser.executeJavaScript("window.resizeBy(5,5);", url, 1);

                    InputContext.getInstance().selectInputMethod(Locale.ENGLISH);
                });
            }

        }, cefBrowser);

        jbCefBrowser.loadURL(url);

        Disposer.register(getDisposable(), jbCefClient);
        Disposer.register(getDisposable(), jbCefBrowser);

        return jbCefBrowser;
    }

    private void setTokenCookie(JBCefBrowser jbCefBrowser, String url) {
        String kubesphereToken = ConfigUtil.getKubesphereToken();
        CefCookie cefCookie = new CefCookie("token", kubesphereToken,
                "host-kslb.mh.bluemoon.com.cn", "/", false, true,
                null, null, false, null);
        jbCefBrowser.getJBCefCookieManager().getCefCookieManager().setCookie(url, cefCookie);
    }

    private String interceptWs() {
        return "     let accessor = Object.getOwnPropertyDescriptor(WebSocket.prototype, 'onopen');\n" +
                "    Object.defineProperty(WebSocket.prototype, 'onopen', {\n" +
                "        get: function () {\n" +
                "            return accessor.get.call(this);\n" +
                "        },\n" +
                "        set: function () {\n" +
                "            window.lastWsObj=this;\n" +
                "        },\n" +
                "        configurable: true\n" +
                "    });";
    }

    private Pair<Pair<String, String>, Pair<String, String>> getLogPath(@Nullable Project project, String selectService) {
        K8sOptions k8sOptions = ConfigUtil.getK8sOptions(project);
        String logDir = k8sOptions.getLogDir();
        String debugFile = MessageFormat.format(k8sOptions.getLogDebugFile(), selectService);
        String errorFile = MessageFormat.format(k8sOptions.getLogErrorFile(), selectService);
        String infoFile = MessageFormat.format(k8sOptions.getLogInfoFile(), selectService);
        Pair<String, String> p1 = Pair.create(logDir, debugFile);
        Pair<String, String> p2 = Pair.create(errorFile, infoFile);
        return Pair.create(p1, p2);
    }

    private void executeCommand(String command, CefBrowser cefBrowser, String url) {
        char[] charArray = command.toCharArray();
        String data = "window.lastWsObj.send(JSON.stringify({'Op':'stdin','Data':'%s'}))";
        for (char c : charArray) {
            if (c == '\t') {
                String tmp = String.format(data, "\\t");
                cefBrowser.executeJavaScript(tmp, url, 1);
            } else if (c == '\n') {
                String tmp = String.format(data, "\\n");
                cefBrowser.executeJavaScript(tmp, url, 1);
            } else {
                String tmp = String.format(data, c);
                cefBrowser.executeJavaScript(tmp, url, 1);
            }
        }
        cefBrowser.setFocus(true);
    }

    @Override
    protected Action @NotNull [] createActions() {
        return new Action[0];
    }

    @Override
    protected @Nullable ActionListener createCancelAction() {
        return null;
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return mainPanel;
    }
}
