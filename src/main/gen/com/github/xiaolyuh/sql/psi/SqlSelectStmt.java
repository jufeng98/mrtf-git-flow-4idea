// This is a generated file. Not intended for manual editing.
package com.github.xiaolyuh.sql.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SqlSelectStmt extends PsiElement {

  @Nullable
  SqlCompoundResultColumn getCompoundResultColumn();

  @NotNull
  List<SqlExpr> getExprList();

  @Nullable
  SqlJoinClause getJoinClause();

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
  PsiElement getGroup();

  @Nullable
  PsiElement getSelect();

  @Nullable
  PsiElement getValues();

  @Nullable
  PsiElement getWhere();

}
