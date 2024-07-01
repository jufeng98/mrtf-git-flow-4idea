// This is a generated file. Not intended for manual editing.
package com.github.xiaolyuh.sql.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.github.xiaolyuh.sql.psi.SqlTypes.*;
import com.github.xiaolyuh.sql.psi.*;

public class SqlFunctionExprImpl extends SqlExprImpl implements SqlFunctionExpr {

  public SqlFunctionExprImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull SqlVisitor visitor) {
    visitor.visitFunctionExpr(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SqlVisitor) accept((SqlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<SqlExpr> getExprList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SqlExpr.class);
  }

  @Override
  @NotNull
  public SqlFunctionName getFunctionName() {
    return findNotNullChildByClass(SqlFunctionName.class);
  }

  @Override
  @Nullable
  public PsiElement getDistinct() {
    return findChildByType(DISTINCT);
  }

}