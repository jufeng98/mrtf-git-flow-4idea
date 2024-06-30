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

public class SqlLiteralExprImpl extends SqlExprImpl implements SqlLiteralExpr {

  public SqlLiteralExprImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull SqlVisitor visitor) {
    visitor.visitLiteralExpr(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SqlVisitor) accept((SqlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public SqlLiteralValue getLiteralValue() {
    return findNotNullChildByClass(SqlLiteralValue.class);
  }

}
