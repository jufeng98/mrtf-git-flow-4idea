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

public class SqlColumnExprImpl extends SqlExprImpl implements SqlColumnExpr {

  public SqlColumnExprImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull SqlVisitor visitor) {
    visitor.visitColumnExpr(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SqlVisitor) accept((SqlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public SqlColumnName getColumnName() {
    return findNotNullChildByClass(SqlColumnName.class);
  }

  @Override
  @Nullable
  public SqlDatabaseName getDatabaseName() {
    return findChildByClass(SqlDatabaseName.class);
  }

  @Override
  @Nullable
  public SqlTableName getTableName() {
    return findChildByClass(SqlTableName.class);
  }

}
