// This is a generated file. Not intended for manual editing.
package com.github.xiaolyuh.sql.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.alecstrong.sql.psi.core.psi.SqlCompositeElement;

public interface SqlCreateIndexStmt extends SqlCompositeElement {

  @Nullable
  SqlDatabaseName getDatabaseName();

  @Nullable
  SqlExpr getExpr();

  @NotNull
  SqlIndexName getIndexName();

  @NotNull
  List<SqlIndexedColumn> getIndexedColumnList();

  @Nullable
  SqlTableName getTableName();

  @NotNull
  PsiElement getCreate();

  @Nullable
  PsiElement getExists();

  @Nullable
  PsiElement getIf();

  @NotNull
  PsiElement getIndex();

  @Nullable
  PsiElement getNot();

  @Nullable
  PsiElement getOn();

  @Nullable
  PsiElement getUnique();

  @Nullable
  PsiElement getWhere();

}
