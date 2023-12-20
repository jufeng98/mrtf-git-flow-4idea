package com.github.xiaolyuh.ui;

import com.github.xiaolyuh.utils.ConfigUtil;
import com.github.xiaolyuh.utils.ExecutorUtils;
import com.github.xiaolyuh.utils.KubesphereUtils;
import com.github.xiaolyuh.vo.InstanceVo;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.Pair;
import com.intellij.ui.jcef.JBCefBrowser;
import com.intellij.ui.jcef.JBCefClient;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefLoadHandlerAdapter;
import org.cef.handler.CefRequestHandlerAdapter;
import org.cef.network.CefCookie;
import org.cef.network.CefRequest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.concurrent.TimeUnit;

public class JcefK8sConsoleDialog extends DialogWrapper {
    private JPanel contentPanel;
    private JPanel mainPanel;
    private JButton logBtn;
    private JButton debugBtn;
    private JButton errorBtn;
    private JButton infoBtn;
    private JButton closeBtn;

    public JcefK8sConsoleDialog(InstanceVo instanceVo, String runsUrl, @Nullable Project project, String selectService) {
        super(project);
        setModal(false);
        selectService = selectService.toLowerCase();
        setTitle(instanceVo.getDesc() + ":" + instanceVo.getName());
        init();

        String url = getUrl(runsUrl, instanceVo, selectService);

        JBCefBrowser jbCefBrowser = initJcef(url);
        CefBrowser cefBrowser = jbCefBrowser.getCefBrowser();

        String namespace = KubesphereUtils.findNamespace(runsUrl);
        Pair<Pair<String, String>, Pair<String, String>> pair = getLogPath(namespace, selectService);
        String logPath = pair.getFirst().getFirst();

        logBtn.addActionListener(e -> executeCommand("cd " + logPath, cefBrowser, url));
        debugBtn.addActionListener(e -> executeCommand("less " + logPath + "/" + pair.getFirst().getSecond(), cefBrowser, url));
        errorBtn.addActionListener(e -> executeCommand("less " + logPath + "/" + pair.getSecond().getFirst(), cefBrowser, url));
        infoBtn.addActionListener(e -> executeCommand("less " + logPath + "/" + pair.getSecond().getSecond(), cefBrowser, url));
        closeBtn.addActionListener(e -> JcefK8sConsoleDialog.this.close(CLOSE_EXIT_CODE, true));

        contentPanel.add(jbCefBrowser.getComponent(), BorderLayout.CENTER);
    }

    public static String getUrl(String runsUrl, InstanceVo instanceVo, String selectService) {
        String namespace = KubesphereUtils.findNamespace(runsUrl);
        String urlTemplate = "http://host-kslb.mh.bluemoon.com.cn/terminal/cluster/sim-1/projects/%s/pods/%s/containers/%s";
        return String.format(urlTemplate, namespace, instanceVo.getName(), selectService);
    }

    public static JBCefBrowser initJcef(String url) {
        JBCefBrowser jbCefBrowser = new JBCefBrowser();
        JBCefClient jbCefClient = jbCefBrowser.getJBCefClient();
        CefBrowser cefBrowser = jbCefBrowser.getCefBrowser();

        jbCefClient.addLoadHandler(new CefLoadHandlerAdapter() {

            @Override
            public void onLoadEnd(CefBrowser browser, CefFrame frame, int httpStatusCode) {
                browser.executeJavaScript(interceptWs(), url, 1);
                ExecutorUtils.addTask(() -> {
                    TimeUnit.MILLISECONDS.sleep(100);
                    browser.executeJavaScript("document.getElementsByClassName('_1jhjCgxv3S5BKvppnuJpUJ')[0].style.display='none';", url, 1);
                    browser.executeJavaScript("document.getElementsByClassName('_2s7aowlSdhOYMXS2Jm7vG6')[0].style.display='none';", url, 1);
                    browser.executeJavaScript("document.getElementsByClassName('_1L_vmkiDzIokO_PY6QgCyV')[0].style.top='5px';", url, 1);
                    browser.executeJavaScript("window.resizeBy(5,5);", url, 1);
                });
            }

        }, cefBrowser);

        jbCefClient.addRequestHandler(new CefRequestHandlerAdapter() {
            @Override
            public boolean onBeforeBrowse(CefBrowser browser, CefFrame frame, CefRequest request, boolean user_gesture,
                                          boolean is_redirect) {
                String kubesphereToken = ConfigUtil.getKubesphereToken();
                CefCookie cefCookie = new CefCookie("token", kubesphereToken,
                        "host-kslb.mh.bluemoon.com.cn", "/", false, true,
                        null, null, false, null);

                jbCefBrowser.getJBCefCookieManager().getCefCookieManager().setCookie(url, cefCookie);
                return false;
            }
        }, cefBrowser);

        jbCefBrowser.loadURL(url);
        return jbCefBrowser;
    }

    private static String interceptWs() {
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

    private Pair<Pair<String, String>, Pair<String, String>> getLogPath(String namespace, String selectService) {
        String logPath = "/home/appadm/logs";
        String debugFile;
        String errorFile;
        String infoFile;
        if (namespace.equals("washingservice-parent")) {
            if (selectService.equals("washingservicemana")) {
                logPath = "/data/server/website/bluemoonMana/logs";
            } else if (selectService.equals("washingservice-controller")) {
                logPath = "/data/server/website/bluemoon-control/logs";
            } else {
                logPath = "/data/server/website";
            }
            debugFile = "sql.log";
            errorFile = "err.log";
            infoFile = "out.log";
        } else if (namespace.equals("hotelwash")) {
            logPath = "/opt/webapp/tmp/logs";
            debugFile = selectService + ".log";
            errorFile = debugFile;
            infoFile = debugFile;
        } else {
            debugFile = selectService + "-debug.log";
            errorFile = selectService + "-error.log";
            infoFile = selectService + ".log";
        }
        Pair<String, String> p1 = Pair.create(logPath, debugFile);
        Pair<String, String> p2 = Pair.create(errorFile, infoFile);
        return Pair.create(p1, p2);
    }

    private void executeCommand(String command, CefBrowser cefBrowser, String url) {
        char[] charArray = command.toCharArray();
        String data = "window.lastWsObj.send(JSON.stringify({'Op':'stdin','Data':'%s'}))";
        for (char c : charArray) {
            String tmp = String.format(data, c);
            cefBrowser.executeJavaScript(tmp, url, 1);
            try {
                TimeUnit.MILLISECONDS.sleep(5);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        cefBrowser.executeJavaScript(String.format(data, "\\r"), url, 1);
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
