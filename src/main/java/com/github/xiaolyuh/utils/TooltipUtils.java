package com.github.xiaolyuh.utils;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;

public class TooltipUtils {

    public static void showTooltip(String msg, Project project) {
        Editor textEditor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (textEditor == null) {
            return;
        }
        HintManager.getInstance().showInformationHint(textEditor, msg);
    }

}
