// This is a generated file. Not intended for manual editing.
package com.github.xiaolyuh.sql.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.github.xiaolyuh.sql.psi.SqlTypes.*;
import com.alecstrong.sql.psi.core.psi.mixins.MutatorMixin;
import com.github.xiaolyuh.sql.psi.*;

public class SqlUpdateStmtImpl extends MutatorMixin implements SqlUpdateStmt {

  public SqlUpdateStmtImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SqlVisitor visitor) {
    visitor.visitUpdateStmt(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SqlVisitor) accept((SqlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public SqlColumnName getColumnName() {
    return findChildByClass(SqlColumnName.class);
  }

  @Override
  @Nullable
  public SqlExpr getExpr() {
    return findChildByClass(SqlExpr.class);
  }

  @Override
  @NotNull
  public SqlQualifiedTableName getQualifiedTableName() {
    return findNotNullChildByClass(SqlQualifiedTableName.class);
  }

  @Override
  @Nullable
  public SqlSetterExpression getSetterExpression() {
    return findChildByClass(SqlSetterExpression.class);
  }

  @Override
  @NotNull
  public List<SqlUpdateStmtSubsequentSetter> getUpdateStmtSubsequentSetterList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SqlUpdateStmtSubsequentSetter.class);
  }

  @Override
  @Nullable
  public SqlWithClause getWithClause() {
    return findChildByClass(SqlWithClause.class);
  }

  @Override
  @Nullable
  public PsiElement getRollback() {
    return findChildByType(ROLLBACK);
  }

  @Override
  @Nullable
  public PsiElement getSet() {
    return findChildByType(SET);
  }

  @Override
  @NotNull
  public PsiElement getUpdate() {
    return findNotNullChildByType(UPDATE);
  }

  @Override
  @Nullable
  public PsiElement getWhere() {
    return findChildByType(WHERE);
  }

}
