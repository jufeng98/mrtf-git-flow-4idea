package com.github.xiaolyuh.utils;

import com.google.common.collect.Sets;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.PropertiesUtil;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiLiteralUtil;
import com.intellij.psi.util.PsiTreeUtil;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ValueUtils {
    public static final Set<String> INTEREST_ANNO_SET = Sets.newHashSet(
            "org.springframework.beans.factory.annotation.Value",
            "com.ctrip.framework.apollo.spring.annotation.ApolloJsonValue"
    );
    public static final String DOLLAR_START = "${";
    public static final String DOLLAR_END = "}";

    /**
     * @return Triple(Apollo key, key文本范围, Apollo value的PsiElement)
     */
    public static @Nullable Triple<String, TextRange, PsiElement> findApolloConfig(PsiElement element) {
        if (!(element instanceof PsiLiteralExpression)) {
            return null;
        }

        PsiLiteralExpression psiLiteralExpression = (PsiLiteralExpression) element;

        if (noValueAnnotation(psiLiteralExpression)) {
            return null;
        }

        String value = PsiLiteralUtil.getStringLiteralContent(psiLiteralExpression);
        if (value == null) {
            return null;
        }

        int startIdx = value.indexOf(DOLLAR_START);
        if (startIdx == -1) {
            return null;
        }

        TextRange textRange = createExpressionTextRange(value, startIdx);

        String key = value.substring(textRange.getStartOffset() - 1, textRange.getEndOffset() - 1);

        Module module = ModuleUtil.findModuleForFile(psiLiteralExpression.getContainingFile());

        String appId = getApolloAppId(module);
        if (appId == null) {
            return null;
        }

        PsiElement psiElement = getApolloConfig(appId, psiLiteralExpression.getProject(), key);
        if (psiElement == null) {
            return null;
        }

        return Triple.of(key, textRange, psiElement);
    }

    private static boolean noValueAnnotation(PsiLiteralExpression psiLiteralExpression) {
        PsiAnnotation psiAnnotation = PsiTreeUtil.getParentOfType(psiLiteralExpression, PsiAnnotation.class);
        if (psiAnnotation == null) {
            return true;
        }

        String qualifiedName = psiAnnotation.getQualifiedName();
        return !INTEREST_ANNO_SET.contains(qualifiedName);
    }

    private static TextRange createExpressionTextRange(String value, int startIdx) {
        int endIdx;
        int separatorIndex = value.indexOf(":");
        if (separatorIndex != -1) {
            endIdx = separatorIndex + DOLLAR_END.length();
        } else {
            endIdx = value.indexOf(DOLLAR_END, startIdx) + DOLLAR_END.length();
        }
        return new TextRange(startIdx + DOLLAR_START.length() + 1, endIdx);
    }

    private static String getApolloAppId(Module module) {
        Collection<VirtualFile> files = FilenameIndex.getVirtualFilesByName("app.properties",
                GlobalSearchScope.moduleScope(module));
        if (files.isEmpty()) {
            return null;
        }

        List<VirtualFile> virtualFiles = files.stream()
                .filter(it -> {
                    String path = it.getPath();
                    return path.contains("main");
                })
                .collect(Collectors.toList());
        if (files.isEmpty()) {
            return null;
        }

        PsiManager psiManager = PsiManager.getInstance(module.getProject());
        PsiFile psiFile = psiManager.findFile(virtualFiles.get(0));
        if (psiFile == null) {
            return null;
        }

        String text = psiFile.getText();
        Map<String, String> map;
        try {
            map = PropertiesUtil.loadProperties(new StringReader(text));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return map.get("app.id");
    }

    private static @Nullable PsiElement getApolloConfig(String appId, Project project, String key) {
        File path = new File("C:\\opt\\data\\" + appId + "\\config-cache");
        File[] childFiles = path.listFiles();
        if (childFiles == null) {
            return null;
        }

        for (File childFile : childFiles) {
            if (!childFile.getName().startsWith(appId + "+kubesphere_test+")) {
                continue;
            }

            VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByIoFile(childFile);
            if (virtualFile == null) {
                continue;
            }

            try {
                // 不知道如何引入 com.intellij.lang.properties.psi.PropertiesFile 接口,只能用反射了
                PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
                if (psiFile == null) {
                    continue;
                }

                Method method = psiFile.getClass().getDeclaredMethod("findPropertyByKey", String.class);
                method.setAccessible(true);
                Object prop = method.invoke(psiFile, key);
                if (prop == null) {
                    continue;
                }

                method = prop.getClass().getDeclaredMethod("getPsiElement");
                method.setAccessible(true);
                return (PsiElement) method.invoke(prop);
            } catch (Exception ignored) {
            }
        }

        return null;
    }
}
