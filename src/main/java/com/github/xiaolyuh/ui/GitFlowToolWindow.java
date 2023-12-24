package com.github.xiaolyuh.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NotNull;

public class GitFlowToolWindow implements ToolWindowFactory {
    @Override
    public boolean shouldBeAvailable(@NotNull Project project) {
        return false;
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ContentManager contentManager = toolWindow.getContentManager();
        GitFlowToolWindowDialog dialog = new GitFlowToolWindowDialog(project, toolWindow);

        contentManager.getComponent().add(dialog.createCenterPanel());

//        Content tab1 = contentManager.getFactory()
//                .createContent(comp.getMainPanel(), "Tab1", false);
//        contentManager.addContent(tab1);
//
//        Content tab2 = contentManager.getFactory()
//                .createContent(comp.getMainPanel(), "Tab2", false);
//        contentManager.addContent(tab2);
    }

}
