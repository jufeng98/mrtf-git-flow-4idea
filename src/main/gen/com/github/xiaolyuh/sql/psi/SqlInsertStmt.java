// This is a generated file. Not intended for manual editing.
package com.github.xiaolyuh.sql.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.alecstrong.sql.psi.core.psi.SqlCompositeElement;

public interface SqlInsertStmt extends SqlCompositeElement {

  @NotNull
  List<SqlColumnName> getColumnNameList();

  @Nullable
  SqlCompoundSelectStmt getCompoundSelectStmt();

  @Nullable
  SqlDatabaseName getDatabaseName();

  @NotNull
  SqlTableName getTableName();

  @NotNull
  List<SqlValuesExpression> getValuesExpressionList();

  @Nullable
  SqlWithClause getWithClause();

  @Nullable
  PsiElement getDefault();

  @Nullable
  PsiElement getInsert();

  @NotNull
  PsiElement getInto();

  @Nullable
  PsiElement getRollback();

  @Nullable
  PsiElement getValues();

}
