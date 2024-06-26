// This is a generated file. Not intended for manual editing.
package com.github.xiaolyuh.sql.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.alecstrong.sql.psi.core.psi.SqlCompositeElement;

public interface SqlDeleteStmtLimited extends SqlCompositeElement {

  @NotNull
  List<SqlExpr> getExprList();

  @NotNull
  List<SqlOrderingTerm> getOrderingTermList();

  @NotNull
  SqlQualifiedTableName getQualifiedTableName();

  @Nullable
  SqlWithClause getWithClause();

  @Nullable
  PsiElement getBy();

  @NotNull
  PsiElement getDelete();

  @NotNull
  PsiElement getFrom();

  @Nullable
  PsiElement getLimit();

  @Nullable
  PsiElement getOffset();

  @Nullable
  PsiElement getOrder();

  @Nullable
  PsiElement getWhere();

}
