package com.github.xiaolyuh.sql.psi.impl;

import com.github.xiaolyuh.utils.TooltipUtils;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.psi.PsiElement;

public class FakePsiElement extends ASTWrapperPsiElement {
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
