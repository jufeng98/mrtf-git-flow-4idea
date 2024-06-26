package com.github.xiaolyuh.ui;

import com.github.xiaolyuh.utils.NotifyUtil;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.lang.Language;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowId;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.impl.java.stubs.index.JavaAnnotationIndex;
import com.intellij.psi.impl.java.stubs.index.JavaStubIndexKeys;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiSearchHelper;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.search.searches.AllClassesSearch;
import com.intellij.psi.search.searches.ClassInheritorsSearch;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.util.DocumentUtil;
import com.intellij.util.Query;
import com.intellij.util.indexing.FileBasedIndex;
import com.intellij.util.indexing.ID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@SuppressWarnings("DialogTitleCapitalization")
public class SampleDialog extends DialogWrapper {

    private JPanel mainPanel;
    private JButton chooseFileBtn;
    private JButton openPopupBtn;
    private JButton openNotificationBtn;
    private JButton activeToolWindowBtn;
    private JButton toolWindowBalloonBtn;
    private JButton projectBtn;
    private JButton msgBtn;
    private JButton fileNameIdxBtn;
    private JButton findBtn;
    private JButton btn3;
    private JButton editorBtn;
    private JButton chromeBtn;
    private JButton getVfs;
    private JButton getDoc;
    private JButton fileBasedIndexesButton;
    private JButton stubIndexesButton;

    @SuppressWarnings("DataFlowIssue")
    public SampleDialog(AnActionEvent event) {
        super(true);
        setTitle("测试DialogWrapper");

        Project project = event.getProject();
        Objects.requireNonNull(project);

        init();

        activeToolWindowBtn.addActionListener(e -> {
            // 激活侧边栏窗口
            activeToolWindow(project);
        });

        chooseFileBtn.addActionListener((e) -> {
            // 选择文件
            chooseFile(project);
        });

        openPopupBtn.addActionListener((e) -> showPopup());

        openNotificationBtn.addActionListener((e) -> {
            NotifyUtil.notifySuccess(project, "这里是成功提示");
            NotifyUtil.notifyError(project, "这里是错误提示");
            // 没有右下角小窗口弹出,只会在notifications
            NotifyUtil.notifyInfo(project, "这里是消息提示");
        });

        toolWindowBalloonBtn.addActionListener(e -> showVcsBalloon(project, "<h2>这是Tool window balloon</h2>"));

        projectBtn.addActionListener(e -> showProjectInfo(project));

        msgBtn.addActionListener(e -> Messages.showInfoMessage(project, "这是说明啊", "标题"));

        fileNameIdxBtn.addActionListener(e -> {
            // 通过FilenameIndex寻找任意文件
            Collection<VirtualFile> files = FilenameIndex
                    .getVirtualFilesByName("git-flow-k8s.json", GlobalSearchScope.projectScope(project));

            showVcsBalloon(project, String.format("<div>%s</div>", String.join("<br/>", files.toString())));
        });

        findBtn.addActionListener(e -> {
            // 根据全限定名寻找class
            PsiClass psiClass = JavaPsiFacade.getInstance(project)
                    .findClass("java.util.LinkedList", GlobalSearchScope.everythingScope(project));

            PsiJavaFile javaFile = (PsiJavaFile) psiClass.getContainingFile();
            PsiPackage psiPackage = JavaPsiFacade.getInstance(project).findPackage(javaFile.getPackageName());
            System.out.println(psiPackage);

            // 寻找类的所有实现
            Query<PsiClass> search = ClassInheritorsSearch.search(psiClass);
            PsiClass[] array = search.toArray(new PsiClass[0]);

            showVcsBalloon(project, String.format("<div>LinkedList的所有实现类</div><div>%s</div>", Arrays.toString(array)));
        });

        btn3.addActionListener(e -> {
            // 根据类名寻找类
            @NotNull PsiClass[] psiClasses = PsiShortNamesCache.getInstance(project)
                    .getClassesByName("StringEscapeUtils", GlobalSearchScope.everythingScope(project));

            showVcsBalloon(project, String.format("<div>%s</div>", Arrays.toString(psiClasses)));
        });

        editorBtn.addActionListener(e -> {
            close(CLOSE_EXIT_CODE);
            VirtualFile[] openFiles = FileEditorManager.getInstance(project).getOpenFiles();
            if (openFiles.length == 0) {
                return;
            }

            VirtualFile virtualFile = openFiles[0];
            EditorDialog editorDialog = new EditorDialog(project, virtualFile);
            editorDialog.show();
        });

        chromeBtn.addActionListener(e -> close(CLOSE_EXIT_CODE));

        getVfs.addActionListener(e -> getVfs(event));

        getDoc.addActionListener(e -> getDoc(event));

        fileBasedIndexesButton.addActionListener(e -> handleFileBasedIndexes(event));
        stubIndexesButton.addActionListener(e -> handleStubIndexes(event));
    }

    @SuppressWarnings("DataFlowIssue")
    private void handleStubIndexes(AnActionEvent event) {
        Project project = event.getProject();
        StubIndex stubIndex = StubIndex.getInstance();

        Collection<String> keys = stubIndex.getAllKeys(JavaStubIndexKeys.ANNOTATIONS, project);

        VirtualFile file = FileEditorManager.getInstance(project).getSelectedEditor().getFile();
        Module module = ModuleUtilCore.findModuleForFile(file, project);

        Query<PsiClass> query = AllClassesSearch.search(GlobalSearchScope.moduleScope(module), project);

        JavaAnnotationIndex javaAnnotationIndex = JavaAnnotationIndex.getInstance();
        Collection<PsiAnnotation> psiAnnotations = javaAnnotationIndex.get("AopLog", project, GlobalSearchScope.projectScope(project));

//        VirtualFileGist<Object> virtualFileGist = GistManager.getInstance().newVirtualFileGist();
//        Object fileData = virtualFileGist.getFileData(project, file);
//        PsiFileGist<Object> psiFileGist = GistManager.getInstance().newPsiFileGist();
//        Object fileData1 = psiFileGist.getFileData(file);

        String s = keys.iterator().next() + "~" + query.iterator().next() + "~" + psiAnnotations.iterator().next();
        showVcsBalloon(project, String.format("<div>%s</div>", s));
    }

    @SuppressWarnings("DataFlowIssue")
    private void handleFileBasedIndexes(AnActionEvent event) {
        Project project = event.getProject();
        FileBasedIndex fileBasedIndex = FileBasedIndex.getInstance();
        ID<Object, Object> id = ID.findByName("filetypes");
        Collection<Object> keys = fileBasedIndex.getAllKeys(id, project);
        FileType key = (FileType) keys.stream()
                .filter(it -> it.toString().toLowerCase().contains("xml"))
                .collect(Collectors.toList())
                .get(0);

        List<Object> values = fileBasedIndex.getValues(id, key, GlobalSearchScope.everythingScope(project));

        //Word Index
        PsiSearchHelper psiSearchHelper = PsiSearchHelper.getInstance(event.getProject());
        PsiElement[] psiElements = psiSearchHelper.findCommentsContainingIdentifier("用例相关的操作", GlobalSearchScope.projectScope(project));

        // File Type Index
        Collection<VirtualFile> files = FileTypeIndex.getFiles(XmlFileType.INSTANCE, GlobalSearchScope.projectScope(project));

        String s = keys.iterator().next() + ":" + values.get(0) + "~" + Arrays.toString(psiElements) + "~" + files.iterator().next();
        showVcsBalloon(project, String.format("<div>%s</div>", s));
    }

    @SuppressWarnings("DataFlowIssue")
    private void getDoc(AnActionEvent e) {

        Document document = e.getData(CommonDataKeys.EDITOR).getDocument();
        System.out.println(document);

        VirtualFile virtualFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        PsiJavaFile psiFile = (PsiJavaFile) PsiManager.getInstance(e.getProject()).findFile(virtualFile);

        PsiFile psiFile1 = PsiFileFactory.getInstance(e.getProject())
                .createFileFromText(Language.findLanguageByID("json"), "{}");
        System.out.println(psiFile1);


        Document document1 = PsiDocumentManager.getInstance(e.getProject()).getDocument(psiFile);
        System.out.println(document1);

        Document document2 = FileDocumentManager.getInstance().getDocument(virtualFile);
        System.out.println(document2);

        Document document3 = EditorFactory.getInstance().createDocument("hello world");
        System.out.println(document3);

        boolean validOffset = DocumentUtil.isValidOffset(1, document1);
        System.out.println(validOffset);
    }

    @SuppressWarnings("DataFlowIssue")
    private void getVfs(AnActionEvent e) {
        Project project = e.getRequiredData(CommonDataKeys.PROJECT);
        Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);

        VirtualFile virtualFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);

        VirtualFile virtualFile1 = FileDocumentManager.getInstance().getFile(editor.getDocument());
        System.out.println(virtualFile1);

        PsiJavaFile psiFile = (PsiJavaFile) PsiManager.getInstance(project).findFile(virtualFile);
        VirtualFile virtualFile2 = psiFile.getVirtualFile();
        System.out.println(virtualFile2);

        Collection<VirtualFile> files = FilenameIndex
                .getVirtualFilesByName("git-flow-k8s.json", GlobalSearchScope.projectScope(project));
        System.out.println(files);

        VirtualFile virtualFile3 = LocalFileSystem.getInstance().findFileByIoFile(new File("git-flow-k8s.json"));
        System.out.println(virtualFile3);

        VirtualFile virtualFile4 = VirtualFileManager.getInstance().findFileByNioPath(Paths.get("git-flow-k8s.json"));
        System.out.println(virtualFile4);

        VirtualFile userHomeDir = VfsUtil.getUserHomeDir();
        System.out.println(userHomeDir);

        try {
            String s = VfsUtilCore.loadText(virtualFile);
            System.out.println(s);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void activeToolWindow(Project project) {
        close(CLOSE_EXIT_CODE);
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        ToolWindow toolWindow = toolWindowManager.getToolWindow("GitflowPlus.toolWindow");
        //noinspection DataFlowIssue
        toolWindow.setAvailable(true);
        toolWindow.activate(() -> {
        });
    }

    private void chooseFile(Project project) {
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

        showVcsBalloon(project, String.format("<div>%s</div>", String.join("<br/>", strings)));
    }

    private void showProjectInfo(Project project) {
        List<String> strings = new ArrayList<>();
        strings.add("basePath:" + project.getBasePath());
        strings.add("name:" + project.getName());
        strings.add("locationHash:" + project.getLocationHash());
        strings.add("presentableUrl:" + project.getPresentableUrl());
        strings.add("projectFilePath:" + project.getProjectFilePath());
        strings.add("projectFile:" + project.getProjectFile());
        strings.add("workspaceFile:" + project.getWorkspaceFile());

        showVcsBalloon(project, String.format("<div>%s</div>", String.join("<br/>", strings)));
    }

    private void showVcsBalloon(Project project, String html) {
        ToolWindowManager.getInstance(project).notifyByBalloon(ToolWindowId.VCS, MessageType.INFO, html);
    }

    private void showPopup() {
        JBPopupFactory.getInstance()
                .createConfirmation("温馨提示", () ->
                                JBPopupFactory.getInstance().createMessage("你选择了ok").showInFocusCenter(),
                        0
                )
                .showInFocusCenter();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return mainPanel;
    }

}
