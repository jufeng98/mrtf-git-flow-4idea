package com.github.xiaolyuh.ui;

import com.github.xiaolyuh.utils.NotifyUtil;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowId;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.search.searches.ClassInheritorsSearch;
import com.intellij.util.Query;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class SampleDialog extends DialogWrapper {

    private JPanel panel1;
    private JButton chooseFileBtn;
    private JButton openPopupBtn;
    private JButton openNotificationBtn;
    private JButton activeToolWindowBtn;
    private JButton toolWindowBalloonBtn;
    private JButton projectBtn;
    private JButton button1;
    private JButton button2;
    private JButton findBtn;
    private JButton btn3;
    private JButton editorBtn;
    private JButton chromeBtn;

    public SampleDialog(@Nullable Project project) {
        super(true);
        setTitle("测试DialogWrapper");
        Objects.requireNonNull(project);

        init();

        activeToolWindowBtn.addActionListener(e -> {
            close(CLOSE_EXIT_CODE);
            ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
            ToolWindow toolWindow = toolWindowManager.getToolWindow("GitflowPlus.toolWindow");
            //noinspection DataFlowIssue
            toolWindow.setAvailable(true);
            toolWindow.activate(() -> {
            });
        });

        chooseFileBtn.addActionListener((e) -> {
            FileChooserDescriptor descriptor = new FileChooserDescriptor(true, false,
                    false, false, false, false);
            VirtualFile virtualFile = FileChooser.chooseFile(descriptor, project, null);
            if (virtualFile == null) {
                return;
            }
            List<String> strings = new ArrayList<>();
            strings.add("getName:" + virtualFile.getName());
            strings.add("getPresentableUrl:" + virtualFile.getPresentableUrl());
            strings.add("getPresentableName:" + virtualFile.getPresentableName());
            strings.add("getPath:" + virtualFile.getPath());
            strings.add("getUrl:" + virtualFile.getUrl());
            strings.add("getBOM:" + Arrays.toString(virtualFile.getBOM()));
            strings.add("getCanonicalPath:" + virtualFile.getCanonicalPath());
            strings.add("getDetectedLineSeparator:" + virtualFile.getDetectedLineSeparator());
            strings.add("getExtension:" + virtualFile.getExtension());
            strings.add("getNameWithoutExtension:" + virtualFile.getNameWithoutExtension());
            ToolWindowManager.getInstance(project).notifyByBalloon(ToolWindowId.VCS,
                    MessageType.INFO, String.format("<div>%s</div>", String.join("<br/>", strings)));
        });

        openPopupBtn.addActionListener((e) -> JBPopupFactory.getInstance()
                .createConfirmation("温馨提示", () ->
                        JBPopupFactory.getInstance().createMessage("你选择了OK").showInFocusCenter(), 0)
                .showInFocusCenter());

        openNotificationBtn.addActionListener((e) -> {
            NotifyUtil.notifySuccess(project, "这里是成功提示");
            NotifyUtil.notifyError(project, "这里是错误提示");
            NotifyUtil.notifyInfo(project, "这里是消息提示");
        });

        toolWindowBalloonBtn.addActionListener(e -> ToolWindowManager.getInstance(project)
                .notifyByBalloon(ToolWindowId.VCS,
                        MessageType.INFO, "<h2>这是Tool window balloon</h2>"));

        projectBtn.addActionListener(e -> {
            List<String> strings = new ArrayList<>();
            strings.add("basePath:" + project.getBasePath());
            strings.add("name:" + project.getName());
            strings.add("locationHash:" + project.getLocationHash());
            strings.add("presentableUrl:" + project.getPresentableUrl());
            strings.add("projectFilePath:" + project.getProjectFilePath());
            strings.add("projectFile:" + project.getProjectFile());
            strings.add("workspaceFile:" + project.getWorkspaceFile());
            ToolWindowManager.getInstance(project).notifyByBalloon(ToolWindowId.VCS,
                    MessageType.INFO, String.format("<div>%s</div>", String.join("<br/>", strings)));
        });

        button1.addActionListener(e -> Messages.showInfoMessage(project, "这是说明啊", "标题"));

        button2.addActionListener(e -> {
            Collection<VirtualFile> files = FilenameIndex.getVirtualFilesByName("git-flow-project.json",
                    GlobalSearchScope.projectScope(project));

            ToolWindowManager.getInstance(project).notifyByBalloon(ToolWindowId.VCS,
                    MessageType.INFO, String.format("<div>%s</div>", String.join("<br/>", files.toString())));
        });
        findBtn.addActionListener(e -> {
            PsiClass aClass = JavaPsiFacade.getInstance(project).findClass("java.util.LinkedList",
                    GlobalSearchScope.everythingScope(project));

            Query<PsiClass> search = ClassInheritorsSearch.search(aClass);
            PsiClass[] array = search.toArray(new PsiClass[0]);
            ToolWindowManager.getInstance(project).notifyByBalloon(ToolWindowId.VCS,
                    MessageType.INFO, String.format("<div>LinkedList的所有实现类</div><div>%s</div>", Arrays.toString(array)));
        });

        btn3.addActionListener(e -> {
            @NotNull PsiClass[] psiClasses = PsiShortNamesCache.getInstance(project).getClassesByName("StringEscapeUtils",
                    GlobalSearchScope.everythingScope(project));

            ToolWindowManager.getInstance(project).notifyByBalloon(ToolWindowId.VCS,
                    MessageType.INFO, String.format("<div>%s</div>", Arrays.toString(psiClasses)));
        });

        editorBtn.addActionListener(e -> {
            close(CLOSE_EXIT_CODE);
            VirtualFile[] openFiles = FileEditorManager.getInstance(project).getOpenFiles();
            if (openFiles.length == 0) {
                return;
            }

            VirtualFile virtualFile = openFiles[0];
            EditorDialog editorDialog = new EditorDialog(project,virtualFile);
            editorDialog.show();
        });

        chromeBtn.addActionListener(e -> {
            close(CLOSE_EXIT_CODE);
            JcefDialog jcefDialog = new JcefDialog(project);
            jcefDialog.show();
        });
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return panel1;
    }
}
