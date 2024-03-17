package com.github.xiaolyuh.cls.step13;

import com.github.xiaolyuh.cls.psi.SimpleProperty;
import com.github.xiaolyuh.cls.step2.SimpleIcons;
import com.intellij.ide.IconProvider;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

final class SimplePropertyIconProvider extends IconProvider {

    @Override
    public @Nullable Icon getIcon(@NotNull PsiElement element, int flags) {
        return element instanceof SimpleProperty ? SimpleIcons.FILE : null;
    }

}
