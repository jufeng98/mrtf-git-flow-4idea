// This is a generated file. Not intended for manual editing.
package com.github.xiaolyuh.sql.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.alecstrong.sql.psi.core.psi.SqlCompositeElement;

public interface SqlAlterTableStmt extends SqlCompositeElement {

  @Nullable
  SqlColumnDef getColumnDef();

  @Nullable
  SqlDatabaseName getDatabaseName();

  @Nullable
  SqlNewTableName getNewTableName();

  @Nullable
  SqlTableName getTableName();

  @Nullable
  PsiElement getAdd();

  @NotNull
  PsiElement getAlter();

  @Nullable
  PsiElement getColumn();

  @Nullable
  PsiElement getRename();

  @Nullable
  PsiElement getTable();

  @Nullable
  PsiElement getTo();

}
