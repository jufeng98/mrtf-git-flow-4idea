// This is a generated file. Not intended for manual editing.
package com.github.xiaolyuh.sql.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SqlColumnExpr extends SqlExpr {

  @NotNull
  SqlColumnName getColumnName();

  @Nullable
  SqlDatabaseName getDatabaseName();

  @Nullable
  SqlTableName getTableName();

}
