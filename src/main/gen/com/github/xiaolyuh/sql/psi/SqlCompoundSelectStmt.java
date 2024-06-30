// This is a generated file. Not intended for manual editing.
package com.github.xiaolyuh.sql.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.alecstrong.sql.psi.core.psi.QueryElement;
import com.alecstrong.sql.psi.core.psi.SqlCompositeElement;

public interface SqlCompoundSelectStmt extends QueryElement, SqlCompositeElement {

  @NotNull
  List<SqlCompoundOperator> getCompoundOperatorList();

  @NotNull
  List<SqlLimitingTerm> getLimitingTermList();

  @NotNull
  List<SqlOrderingTerm> getOrderingTermList();

  @NotNull
  List<SqlSelectStmt> getSelectStmtList();

  @Nullable
  SqlWithClause getWithClause();

  @Nullable
  PsiElement getBy();

  @Nullable
  PsiElement getLimit();

  @Nullable
  PsiElement getOffset();

  @Nullable
  PsiElement getOrder();

}
