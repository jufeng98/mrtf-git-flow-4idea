// This is a generated file. Not intended for manual editing.
package com.github.xiaolyuh.pcel.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.github.xiaolyuh.pcel.psi.PointcutExpressionTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.github.xiaolyuh.pcel.psi.*;

public class AopPointcutCombinationImpl extends ASTWrapperPsiElement implements AopPointcutCombination {

  public AopPointcutCombinationImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull AopVisitor visitor) {
    visitor.visitPointcutCombination(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof AopVisitor) accept((AopVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public AopPointcut getPointcut() {
    return findChildByClass(AopPointcut.class);
  }

  @Override
  @Nullable
  public PsiElement getAndOperator() {
    return findChildByType(AND_OPERATOR);
  }

  @Override
  @Nullable
  public PsiElement getOrOperator() {
    return findChildByType(OR_OPERATOR);
  }

}
