// This is a generated file. Not intended for manual editing.
package com.github.xiaolyuh.spel.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.github.xiaolyuh.spel.psi.SpelTypes.*;
import com.github.xiaolyuh.spel.SpelPsiElement;
import com.github.xiaolyuh.spel.psi.*;
import com.intellij.psi.PsiReference;

public class SpelFieldNameImpl extends SpelPsiElement implements SpelFieldName {

  public SpelFieldNameImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SpelVisitor visitor) {
    visitor.visitFieldName(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SpelVisitor) accept((SpelVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public PsiElement getIdentifier() {
    return findNotNullChildByType(IDENTIFIER);
  }

  @Override
  public PsiReference @NotNull [] getReferences() {
    return SpelPsiImplUtil.getReferences(this);
  }

}
