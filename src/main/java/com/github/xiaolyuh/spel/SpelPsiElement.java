package com.github.xiaolyuh.spel;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class SpelPsiElement extends ASTWrapperPsiElement {
    public SpelPsiElement(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String toString() {
        return getText();
    }
}
