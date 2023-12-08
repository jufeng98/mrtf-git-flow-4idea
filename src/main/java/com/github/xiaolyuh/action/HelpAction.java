package com.github.xiaolyuh.action;

import com.github.xiaolyuh.i18n.I18n;
import com.github.xiaolyuh.i18n.I18nKey;
import com.github.xiaolyuh.ui.SampleDialog;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.util.IconLoader;
import com.intellij.util.ReflectionUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * 帮助
 *
 * @author yuhao.wang3
 */
public class HelpAction extends AnAction {
    public HelpAction() {
        super("帮助", "帮助", IconLoader.getIcon("/icons/help.svg",
                Objects.requireNonNull(ReflectionUtil.getGrandCallerClass())));
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        super.update(event);
        I18n.init(event.getProject());
        event.getPresentation().setText(I18n.getContent(I18nKey.HELP_ACTION$TEXT));
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        if (true) {
            SampleDialog sampleDialog = new SampleDialog(event.getProject());
            sampleDialog.show();
            return;
        }

        BrowserUtil.browse("https://xiaolyuh.blog.csdn.net/article/details/105150446");
    }
}
