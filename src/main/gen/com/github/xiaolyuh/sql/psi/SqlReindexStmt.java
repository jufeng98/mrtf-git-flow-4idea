// This is a generated file. Not intended for manual editing.
package com.github.xiaolyuh.sql.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.alecstrong.sql.psi.core.psi.SqlCompositeElement;

public interface SqlReindexStmt extends SqlCompositeElement {

  @Nullable
  SqlCollationName getCollationName();

  @Nullable
  SqlDatabaseName getDatabaseName();

  @Nullable
  SqlIndexName getIndexName();

  @Nullable
  SqlTableName getTableName();

  @NotNull
  PsiElement getReindex();

}
