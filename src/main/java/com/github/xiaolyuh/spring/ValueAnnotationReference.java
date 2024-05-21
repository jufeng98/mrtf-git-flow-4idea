package com.github.xiaolyuh.spring;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.util.PropertiesUtil;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public final class ValueAnnotationReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {
    private final String key;

    ValueAnnotationReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
        key = element.getText().substring(textRange.getStartOffset(), textRange.getEndOffset());
    }

    @Override
    public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
        Module module = ModuleUtil.findModuleForFile(myElement.getContainingFile());
        if (module == null) {
            return ResolveResult.EMPTY_ARRAY;
        }

        String appId = getApolloAppId(module);
        if (appId == null) {
            return ResolveResult.EMPTY_ARRAY;
        }

        try {
            PsiElement psiElement = getApolloFileOrConfig(appId);
            if (psiElement == null) {
                return ResolveResult.EMPTY_ARRAY;
            }

            return new ResolveResult[]{new PsiElementResolveResult(psiElement)};
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getApolloAppId(Module module) {
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
                .toList();
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

    private @Nullable PsiElement getApolloFileOrConfig(String appId) throws Exception {
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
                // 不知道如何引入 com.intellij.lang.properties.psi.PropertiesFile接口,只能用反射了
                PsiFile psiFile = PsiManager.getInstance(myElement.getProject()).findFile(virtualFile);
                if (psiFile == null) {
                    continue;
                }

                //IProperty findPropertyByKey(@NotNull @NonNls String key);
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

    @Nullable
    @Override
    public PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        return resolveResults.length == 1 ? resolveResults[0].getElement() : null;
    }

}
