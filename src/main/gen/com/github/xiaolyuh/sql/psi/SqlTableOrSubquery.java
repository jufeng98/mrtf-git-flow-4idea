// This is a generated file. Not intended for manual editing.
package com.github.xiaolyuh.sql.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.alecstrong.sql.psi.core.psi.QueryElement;
import com.alecstrong.sql.psi.core.psi.SqlCompositeElement;

public interface SqlTableOrSubquery extends QueryElement, SqlCompositeElement {

  @Nullable
  SqlCompoundSelectStmt getCompoundSelectStmt();

  @Nullable
  SqlDatabaseName getDatabaseName();

  @Nullable
  SqlIndexName getIndexName();

  @Nullable
  SqlJoinClause getJoinClause();

  @Nullable
  SqlTableAlias getTableAlias();

  @Nullable
  SqlTableName getTableName();

  @NotNull
  List<SqlTableOrSubquery> getTableOrSubqueryList();

  @Nullable
  PsiElement getAs();

  @Nullable
  PsiElement getBy();

  @Nullable
  PsiElement getIndexed();

  @Nullable
  PsiElement getNot();

}
