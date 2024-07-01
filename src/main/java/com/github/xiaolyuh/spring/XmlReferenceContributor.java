package com.github.xiaolyuh.spring;

import com.github.xiaolyuh.utils.ValueUtils;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiUtil;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.ProcessingContext;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class XmlReferenceContributor extends PsiReferenceContributor {

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(XmlAttributeValue.class),
                new ValuePsiReferenceProvider());

        registrar.registerReferenceProvider(PlatformPatterns.psiElement(XmlAttributeValue.class),
                new ImportPsiReferenceProvider());
    }

    /**
     * 处理xml文件里的${...}占位符,关联到Apollo
     */
    public static class ValuePsiReferenceProvider extends PsiReferenceProvider {
        @Override
        public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element,
                                                               @NotNull ProcessingContext context) {
            XmlAttributeValue xmlAttributeValue = (XmlAttributeValue) element;

            Triple<List<String>, TextRange, List<PsiElement>> triple = ValueUtils.findApolloConfig(xmlAttributeValue.getValue(),
                    xmlAttributeValue.getProject(), xmlAttributeValue.getContainingFile());
            if (triple == null) {
                return PsiReference.EMPTY_ARRAY;
            }

            TextRange textRange = triple.getMiddle();
            List<PsiElement> psiElements = triple.getRight();

            return new PsiReference[]{new ValueAnnotationReferenceContributor.ValueAnnotationReference(element, textRange, psiElements)};
        }
    }

    /**
     * 处理xml文件里的import标签resource属性,使其可跳转到对应文件
     */
    public static class ImportPsiReferenceProvider extends PsiReferenceProvider {
        @Override
        public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element,
                                                               @NotNull ProcessingContext context) {
            XmlAttributeValue xmlAttributeValue = (XmlAttributeValue) element;
            PsiElement parent = xmlAttributeValue.getParent();
            if (!(parent instanceof XmlAttribute)) {
                return PsiReference.EMPTY_ARRAY;
            }

            XmlAttribute XmlAttribute = (XmlAttribute) parent;
            if (!XmlAttribute.getName().equals("resource")) {
                return PsiReference.EMPTY_ARRAY;
            }

            XmlTag xmlTag = XmlAttribute.getParent();
            if (!xmlTag.getName().equals("import")) {
                return PsiReference.EMPTY_ARRAY;
            }

            String value = xmlAttributeValue.getValue();
            TextRange textRange = new TextRange(1, value.length() + 1);

            Collection<VirtualFile> files = FilenameIndex.getVirtualFilesByName(value, GlobalSearchScope.projectScope(element.getProject()));
            if (files.isEmpty()) {
                return PsiReference.EMPTY_ARRAY;
            }

            VirtualFile virtualFile = files.iterator().next();
            PsiFile psiFile = PsiUtil.getPsiFile(element.getProject(), virtualFile);

            return new PsiReference[]{new PsiReferenceBase<>(element, textRange) {
                @Override
                public @NotNull PsiElement resolve() {
                    return psiFile;
                }

            }};
        }
    }
}
