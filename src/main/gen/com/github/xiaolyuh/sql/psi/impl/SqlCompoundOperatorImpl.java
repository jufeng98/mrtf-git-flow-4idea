// This is a generated file. Not intended for manual editing.
package com.github.xiaolyuh.sql.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.github.xiaolyuh.sql.psi.SqlTypes.*;
import com.alecstrong.sql.psi.core.psi.SqlCompositeElementImpl;
import com.github.xiaolyuh.sql.psi.*;

public class SqlCompoundOperatorImpl extends SqlCompositeElementImpl implements SqlCompoundOperator {

  public SqlCompoundOperatorImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SqlVisitor visitor) {
    visitor.visitCompoundOperator(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SqlVisitor) accept((SqlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public PsiElement getAll() {
    return findChildByType(ALL);
  }

  @Override
  @Nullable
  public PsiElement getUnion() {
    return findChildByType(UNION);
  }

}
