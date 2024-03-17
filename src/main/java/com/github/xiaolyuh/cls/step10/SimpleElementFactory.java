package com.github.xiaolyuh.cls.step10;

import com.github.xiaolyuh.cls.psi.SimpleProperty;
import com.github.xiaolyuh.cls.step2.SimpleFileType;
import com.github.xiaolyuh.cls.step4.SimpleFile;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;

public class SimpleElementFactory {

    public static SimpleProperty createProperty(Project project, String name) {
        final SimpleFile file = createFile(project, name);
        return (SimpleProperty) file.getFirstChild();
    }

    public static SimpleFile createFile(Project project, String text) {
        String name = "dummy.simple";
        return (SimpleFile) PsiFileFactory.getInstance(project).createFileFromText(name, SimpleFileType.INSTANCE, text);
    }

    public static SimpleProperty createProperty(Project project, String name, String value) {
        final SimpleFile file = createFile(project, name + " = " + value);
        return (SimpleProperty) file.getFirstChild();
    }

    public static PsiElement createCRLF(Project project) {
        final SimpleFile file = createFile(project, "\n");
        return file.getFirstChild();
    }

}
