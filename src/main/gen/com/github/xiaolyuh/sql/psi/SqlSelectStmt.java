// This is a generated file. Not intended for manual editing.
package com.github.xiaolyuh.sql.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.alecstrong.sql.psi.core.psi.QueryElement;
import com.alecstrong.sql.psi.core.psi.SqlCompositeElement;

public interface SqlSelectStmt extends QueryElement, SqlCompositeElement {

  @NotNull
  List<SqlExpr> getExprList();

  @Nullable
  SqlJoinClause getJoinClause();

  @NotNull
  List<SqlResultColumn> getResultColumnList();

  @NotNull
  List<SqlValuesExpression> getValuesExpressionList();

  @Nullable
  PsiElement getAll();

  @Nullable
  PsiElement getBy();

  @Nullable
  PsiElement getDistinct();

  @Nullable
  PsiElement getFrom();

  @Nullable
  PsiElement getSelect();

  @Nullable
  PsiElement getValues();

  @Nullable
  PsiElement getWhere();

}
