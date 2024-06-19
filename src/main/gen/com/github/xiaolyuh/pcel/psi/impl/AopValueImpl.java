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

public class AopValueImpl extends ASTWrapperPsiElement implements AopValue {

  public AopValueImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull AopVisitor visitor) {
    visitor.visitValue(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof AopVisitor) accept((AopVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public AopExpr getExpr() {
    return findChildByClass(AopExpr.class);
  }

  @Override
  @NotNull
  public AopKind getKind() {
    return findNotNullChildByClass(AopKind.class);
  }

}
