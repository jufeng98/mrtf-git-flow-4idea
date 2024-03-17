package com.github.xiaolyuh.cls.step15;

import com.github.xiaolyuh.cls.psi.SimpleProperty;
import com.github.xiaolyuh.cls.step2.SimpleLanguage;
import com.github.xiaolyuh.cls.step4.SimpleFile;
import com.intellij.icons.AllIcons;
import com.intellij.ide.navigationToolbar.StructureAwareNavBarModelExtension;
import com.intellij.lang.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

final class SimpleStructureAwareNavbar extends StructureAwareNavBarModelExtension {

    @NotNull
    @Override
    protected Language getLanguage() {
        return SimpleLanguage.INSTANCE;
    }

    @Override
    public @Nullable String getPresentableText(Object object) {
        if (object instanceof SimpleFile) {
            return ((SimpleFile) object).getName();
        }
        if (object instanceof SimpleProperty) {
            return ((SimpleProperty) object).getName();
        }

        return null;
    }

    @Override
    @Nullable
    public Icon getIcon(Object object) {
        if (object instanceof SimpleProperty) {
            return AllIcons.Nodes.Property;
        }

        return null;
    }

}
