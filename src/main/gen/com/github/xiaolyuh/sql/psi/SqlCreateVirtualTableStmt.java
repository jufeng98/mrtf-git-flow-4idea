// This is a generated file. Not intended for manual editing.
package com.github.xiaolyuh.sql.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.alecstrong.sql.psi.core.psi.TableElement;

public interface SqlCreateVirtualTableStmt extends TableElement {

  @Nullable
  SqlDatabaseName getDatabaseName();

  @Nullable
  SqlModuleName getModuleName();

  @NotNull
  SqlTableName getTableName();

  @NotNull
  PsiElement getCreate();

  @Nullable
  PsiElement getExists();

  @Nullable
  PsiElement getIf();

  @Nullable
  PsiElement getNot();

  @NotNull
  PsiElement getTable();

  @Nullable
  PsiElement getUsing();

}
