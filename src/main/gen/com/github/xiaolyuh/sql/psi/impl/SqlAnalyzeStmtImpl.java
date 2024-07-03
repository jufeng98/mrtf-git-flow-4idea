// This is a generated file. Not intended for manual editing.
package com.github.xiaolyuh.sql.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.github.xiaolyuh.sql.psi.SqlTypes.*;
import com.github.xiaolyuh.sql.SqlPsiElement;
import com.github.xiaolyuh.sql.psi.*;

public class SqlAnalyzeStmtImpl extends SqlPsiElement implements SqlAnalyzeStmt {

  public SqlAnalyzeStmtImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SqlVisitor visitor) {
    visitor.visitAnalyzeStmt(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SqlVisitor) accept((SqlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public SqlDatabaseName getDatabaseName() {
    return findChildByClass(SqlDatabaseName.class);
  }

  @Override
  @Nullable
  public SqlTableOrIndexName getTableOrIndexName() {
    return findChildByClass(SqlTableOrIndexName.class);
  }

  @Override
  @NotNull
  public PsiElement getAnalyze() {
    return findNotNullChildByType(ANALYZE);
  }

}
