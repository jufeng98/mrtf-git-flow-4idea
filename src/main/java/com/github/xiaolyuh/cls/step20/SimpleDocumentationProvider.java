package com.github.xiaolyuh.cls.step20;

import com.github.xiaolyuh.cls.psi.SimpleProperty;
import com.github.xiaolyuh.cls.step10.SimpleReference;
import com.github.xiaolyuh.cls.step2.SimpleLanguage;
import com.github.xiaolyuh.cls.step6.SimpleUtil;
import com.google.common.collect.Lists;
import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.lang.documentation.DocumentationMarkup;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.JavaTokenType;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaToken;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.presentation.java.SymbolPresentationUtil;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

final class SimpleDocumentationProvider extends AbstractDocumentationProvider {

    /**
     * Attempts to collect any comment elements above the Simple key/value pair.
     */
    public static @NotNull String findDocumentationComment(SimpleProperty property) {
        List<String> result = new LinkedList<>();
        PsiElement element = property.getPrevSibling();
        while (element instanceof PsiComment || element instanceof PsiWhiteSpace) {
            if (element instanceof PsiComment) {
                String commentText = element.getText().replaceFirst("[!# ]+", "");
                result.add(commentText);
            }
            element = element.getPrevSibling();
        }
        return StringUtil.join(Lists.reverse(result), "\n ");
    }

    /**
     * Extracts the key, value, file and documentation comment of a Simple key/value entry and returns
     * a formatted representation of the information.
     */
    @Override
    public @Nullable String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
        if (element instanceof SimpleProperty) {
            final String key = ((SimpleProperty) element).getKey();
            final String value = ((SimpleProperty) element).getValue();
            final String file = SymbolPresentationUtil.getFilePathPresentation(element.getContainingFile());
            final String docComment = SimpleUtil.findDocumentationComment((SimpleProperty) element);

            return renderFullDoc(key, value, file, docComment);
        }
        return null;
    }
    /**
     * Creates the formatted documentation using {@link DocumentationMarkup}. See the Java doc of
     * {@link com.intellij.lang.documentation.DocumentationProvider#generateDoc(PsiElement, PsiElement)} for more
     * information about building the layout.
     */
    private String renderFullDoc(String key, String value, String file, String docComment) {
        StringBuilder sb = new StringBuilder();
        sb.append(DocumentationMarkup.DEFINITION_START);
        sb.append("Simple Property");
        sb.append(DocumentationMarkup.DEFINITION_END);
        sb.append(DocumentationMarkup.CONTENT_START);
        sb.append(value);
        sb.append(DocumentationMarkup.CONTENT_END);
        sb.append(DocumentationMarkup.SECTIONS_START);
        addKeyValueSection("Key:", key, sb);
        addKeyValueSection("Value:", value, sb);
        addKeyValueSection("File:", file, sb);
        addKeyValueSection("Comment:", docComment, sb);
        sb.append(DocumentationMarkup.SECTIONS_END);
        return sb.toString();
    }
    /**
     * Creates a key/value row for the rendered documentation.
     */
    private void addKeyValueSection(String key, String value, StringBuilder sb) {
        sb.append(DocumentationMarkup.SECTION_HEADER_START);
        sb.append(key);
        sb.append(DocumentationMarkup.SECTION_SEPARATOR);
        sb.append("<p>");
        sb.append(value);
        sb.append(DocumentationMarkup.SECTION_END);
    }

    /**
     * Provides documentation when a Simple Language element is hovered with the mouse.
     */
    @Override
    public @Nullable String generateHoverDoc(@NotNull PsiElement element, @Nullable PsiElement originalElement) {
        return generateDoc(element, originalElement);
    }

    /**
     * Provides the information in which file the Simple language key/value is defined.
     */
    @Override
    public @Nullable String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
        if (element instanceof SimpleProperty) {
            final String key = ((SimpleProperty) element).getKey();
            final String file = SymbolPresentationUtil.getFilePathPresentation(element.getContainingFile());
            return "\"" + key + "\" in " + file;
        }
        return null;
    }

    @Override
    public @Nullable PsiElement getCustomDocumentationElement(@NotNull Editor editor,
                                                              @NotNull PsiFile file,
                                                              @Nullable PsiElement context,
                                                              int targetOffset) {
        if (context != null) {
            // In this part the SimpleProperty element
            // is extracted from inside a Java string
            if (context instanceof PsiJavaToken &&
                    ((PsiJavaToken) context).getTokenType().equals(JavaTokenType.STRING_LITERAL)) {
                PsiElement parent = context.getParent();
                PsiReference[] references = parent.getReferences();
                for (PsiReference ref : references) {
                    if (ref instanceof SimpleReference) {
                        PsiElement property = ref.resolve();
                        if (property instanceof SimpleProperty) {
                            return property;
                        }
                    }
                }
            }
            // In this part the SimpleProperty element is extracted
            // when inside of a .simple file
            else if (context.getLanguage() == SimpleLanguage.INSTANCE) {
                PsiElement property =
                        PsiTreeUtil.getParentOfType(context, SimpleProperty.class);
                if (property != null) {
                    return property;
                }
            }
        }
        return super.getCustomDocumentationElement(
                editor, file, context, targetOffset);
    }
}
