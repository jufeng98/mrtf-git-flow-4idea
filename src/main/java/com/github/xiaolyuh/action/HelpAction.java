package com.github.xiaolyuh.action;

import com.github.xiaolyuh.i18n.I18n;
import com.github.xiaolyuh.i18n.I18nKey;
import com.github.xiaolyuh.ui.SampleDialog;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

import java.awt.event.InputEvent;

/**
 * 帮助
 *
 * @author yuhao.wang3
 */
public class HelpAction extends AnAction {

    @Override
    public void update(@NotNull AnActionEvent event) {
        super.update(event);
        event.getPresentation().setText(I18n.getContent(I18nKey.HELP_ACTION$TEXT));
    }

    @SuppressWarnings("deprecation")
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        if (event.getModifiers() == (InputEvent.ALT_MASK | InputEvent.BUTTON1_MASK)) {
            SampleDialog sampleDialog = new SampleDialog(event);
            sampleDialog.show();
            return;
        }

        BrowserUtil.browse("https://xiaolyuh.blog.csdn.net/article/details/105150446");
    }

}
