// This is a generated file. Not intended for manual editing.
package com.github.xiaolyuh.sql.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.github.xiaolyuh.sql.psi.SqlTypes.*;
import com.alecstrong.sql.psi.core.psi.mixins.BinaryLikeExprMixin;
import com.github.xiaolyuh.sql.psi.*;

public class SqlBinaryLikeExprImpl extends BinaryLikeExprMixin implements SqlBinaryLikeExpr {

  public SqlBinaryLikeExprImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SqlVisitor visitor) {
    visitor.visitBinaryLikeExpr(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SqlVisitor) accept((SqlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public SqlBinaryLikeOperator getBinaryLikeOperator() {
    return findNotNullChildByClass(SqlBinaryLikeOperator.class);
  }

  @Override
  @NotNull
  public List<SqlExpr> getExprList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SqlExpr.class);
  }

  @Override
  @Nullable
  public PsiElement getNot() {
    return findChildByType(NOT);
  }

}
