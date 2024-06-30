// This is a generated file. Not intended for manual editing.
package com.github.xiaolyuh.sql.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.alecstrong.sql.psi.core.psi.SqlCompositeElement;

public interface SqlForeignKeyClause extends SqlCompositeElement {

  @NotNull
  List<SqlColumnName> getColumnNameList();

  @NotNull
  SqlForeignTable getForeignTable();

  @NotNull
  List<SqlIdentifier> getIdentifierList();

  @Nullable
  PsiElement getDeferred();

  @Nullable
  PsiElement getImmediate();

  @Nullable
  PsiElement getNot();

  @NotNull
  PsiElement getReferencesWord();

}
