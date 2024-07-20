package com.github.xiaolyuh.sql.reference;

import com.github.xiaolyuh.sql.psi.SqlTableName;
import com.github.xiaolyuh.utils.TooltipUtils;
import com.intellij.codeInsight.daemon.impl.PsiElementListNavigator;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.ide.util.PsiElementListCellRenderer;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.util.PsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class SqlReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {
    private final PsiElement[] targetPsiElements;

    public SqlReference(@NotNull PsiElement element, @NotNull TextRange textRange, @NotNull PsiElement... targetPsiElements) {
        super(element, textRange);

        if (targetPsiElements.length == 0) {
            targetPsiElements = new PsiElement[]{new FakePsiElement(element)};
        } else if (targetPsiElements[0] instanceof SqlTableName) {
            targetPsiElements = new PsiElement[]{new TableAliasPsiElement(targetPsiElements)};
        }

        this.targetPsiElements = targetPsiElements;
    }

    @Override
    public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
        return Arrays.stream(targetPsiElements)
                .map(PsiElementResolveResult::new)
                .toArray(ResolveResult[]::new);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(true);
        return resolveResults.length == 1 ? resolveResults[0].getElement() : null;
    }

    public static class TableAliasPsiElement extends ASTWrapperPsiElement {
        private final NavigatablePsiElement[] targetPsiElements;

        public TableAliasPsiElement(PsiElement[] targetPsiElements) {
            super(targetPsiElements[0].getNode());
            this.targetPsiElements = Arrays.stream(targetPsiElements)
                    .map(it -> (NavigatablePsiElement) it)
                    .toArray(NavigatablePsiElement[]::new);
        }

        @Override
        public void navigate(boolean requestFocus) {
            VirtualFile virtualFile = PsiUtil.getVirtualFile(this);
            if (virtualFile == null) {
                return;
            }
            String fileName = virtualFile.getName();

            Document sqlDocument = FileDocumentManager.getInstance().getDocument(virtualFile);
            if (sqlDocument == null) {
                return;
            }

            Editor textEditor = FileEditorManager.getInstance(getProject()).getSelectedTextEditor();
            if (textEditor == null) {
                return;
            }

            Document xmlDocument = textEditor.getDocument();
            PsiLanguageInjectionHost injectionHost = InjectedLanguageManager.getInstance(getProject()).getInjectionHost(this);
            int lineNumberHost;
            if (injectionHost != null) {
                //mapper.xml文件里的sql语句的最前面有一个换行符,所以这里不用加1
                lineNumberHost = xmlDocument.getLineNumber(injectionHost.getParent().getTextRange().getStartOffset());
            } else {
                lineNumberHost = 0;
            }

            PsiElementListNavigator.openTargets(textEditor, targetPsiElements,
                    "别名 " + this.getText() + " 共" + targetPsiElements.length + "个引用", "",
                    new PsiElementListCellRenderer<>() {
                        @Override
                        public String getElementText(PsiElement element) {
                            return ApplicationManager.getApplication().runReadAction((Computable<String>) () -> {
                                int lineNumber = sqlDocument.getLineNumber(element.getTextRange().getStartOffset());

                                int lineStartOffset = sqlDocument.getLineStartOffset(lineNumber);
                                int lineEndOffset = sqlDocument.getLineEndOffset(lineNumber);

                                String text = sqlDocument.getText(new TextRange(lineStartOffset, lineEndOffset));

                                return "第" + (lineNumber + lineNumberHost + 1) + "行  " + text.trim();
                            });
                        }

                        @Override
                        protected String getContainerText(PsiElement element, String name) {
                            return fileName;
                        }
                    });
        }
    }

    public static class FakePsiElement extends ASTWrapperPsiElement {
        private final PsiElement element;

        public FakePsiElement(PsiElement element) {
            super(element.getNode());
            this.element = element;
        }

        @Override
        public void navigate(boolean requestFocus) {
            TooltipUtils.showTooltip("无法找到要跳转的声明", element.getProject());
        }
    }
}
