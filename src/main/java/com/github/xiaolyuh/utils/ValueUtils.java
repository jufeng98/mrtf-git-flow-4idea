package com.github.xiaolyuh.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.intellij.lang.properties.IProperty;
import com.intellij.lang.properties.psi.PropertiesFile;
import com.intellij.openapi.module.JavaModuleType;
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
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
     * @return Triple(AppId, key文本范围, Apollo value的PsiElement)
     */
    public static @Nullable Triple<List<String>, TextRange, List<PsiElement>> findApolloConfig(PsiElement element) {
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

        return findApolloConfig(value, psiLiteralExpression.getProject(), psiLiteralExpression.getContainingFile());
    }

    public static @Nullable Triple<List<String>, TextRange, List<PsiElement>> findApolloConfig(String value,
                                                                                               Project project,
                                                                                               PsiFile psiFile) {
        int startIdx = value.indexOf(DOLLAR_START);
        if (startIdx == -1) {
            return null;
        }

        TextRange textRange = createExpressionTextRange(value, startIdx);
        if (textRange == null) {
            return null;
        }

        String key = value.substring(textRange.getStartOffset() - 1, textRange.getEndOffset() - 1);

        Module module = ModuleUtil.findModuleForFile(psiFile);

        // 优先从当前模块寻找
        Pair<String, PsiElement> pairModule = findApolloConfigFromModule(module, project, key);
        if (pairModule != null) {
            return Triple.of(Lists.newArrayList(pairModule.getLeft()), textRange, Lists.newArrayList(pairModule.getRight()));
        }

        // 当前模块找不到,在到项目中寻找
        Pair<List<String>, List<PsiElement>> pairProject = findApolloConfigFromProject(project, module, key);
        if (pairProject != null) {
            return Triple.of(pairProject.getLeft(), textRange, pairProject.getRight());
        }

        return null;
    }

    private static Pair<String, PsiElement> findApolloConfigFromModule(Module module, Project project, String key) {
        String appId = findApolloAppId(module);
        if (appId == null) {
            return null;
        }

        PsiElement psiElement = findApolloConfig(appId, project, key);
        if (psiElement == null) {
            return null;
        }

        return Pair.of(appId, psiElement);
    }

    private static Pair<List<String>, List<PsiElement>> findApolloConfigFromProject(Project project, Module excludeModule,
                                                                                    String key) {
        List<String> apolloAppIds = findApolloAppIds(project, excludeModule);
        if (CollectionUtils.isEmpty(apolloAppIds)) {
            return null;
        }

        List<PsiElement> psiElements = findApolloConfigs(apolloAppIds, project, key);
        if (CollectionUtils.isEmpty(psiElements)) {
            return null;
        }

        return Pair.of(apolloAppIds, psiElements);
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
        int separatorIndex = value.indexOf(":", startIdx);
        if (separatorIndex != -1) {
            endIdx = separatorIndex + DOLLAR_END.length();
        } else {
            endIdx = value.indexOf(DOLLAR_END, startIdx) + DOLLAR_END.length();
        }

        if (endIdx <= startIdx) {
            return null;
        }

        return new TextRange(startIdx + DOLLAR_START.length() + 1, endIdx);
    }

    private static String findApolloAppId(Module module) {
        if (module == null) {
            return null;
        }

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
        if (virtualFiles.isEmpty()) {
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

    private static @Nullable PsiElement findApolloConfig(String appId, Project project, String key) {
        File path = new File("C:\\opt\\data\\" + appId + "\\config-cache");
        File[] childFiles = path.listFiles();
        if (childFiles == null) {
            return null;
        }

        for (File childFile : childFiles) {
            VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByIoFile(childFile);
            if (virtualFile == null) {
                continue;
            }

            try {
                PropertiesFile propertiesFile = (PropertiesFile) PsiManager.getInstance(project).findFile(virtualFile);
                if (propertiesFile == null) {
                    continue;
                }

                IProperty prop = propertiesFile.findPropertyByKey(key);
                if (prop == null) {
                    continue;
                }

                return prop.getPsiElement();
            } catch (Exception ignored) {
            }
        }

        return null;
    }

    public static List<String> findApolloAppIds(Project project, Module excludeModule) {
        Collection<Module> modules = ModuleUtil.getModulesOfType(project, JavaModuleType.getModuleType());
        return modules.stream()
                .filter(it -> it != excludeModule)
                .map(ValueUtils::findApolloAppId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static List<PsiElement> findApolloConfigs(List<String> appIds, Project project, String key) {
        return appIds.stream()
                .map(it -> findApolloConfig(it, project, key))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
