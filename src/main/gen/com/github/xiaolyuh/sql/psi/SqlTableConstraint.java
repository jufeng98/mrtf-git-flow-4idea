// This is a generated file. Not intended for manual editing.
package com.github.xiaolyuh.sql.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.alecstrong.sql.psi.core.psi.SqlCompositeElement;

public interface SqlTableConstraint extends SqlCompositeElement {

  @NotNull
  List<SqlColumnName> getColumnNameList();

  @Nullable
  SqlConflictClause getConflictClause();

  @Nullable
  SqlExpr getExpr();

  @Nullable
  SqlForeignKeyClause getForeignKeyClause();

  @Nullable
  SqlIdentifier getIdentifier();

  @NotNull
  List<SqlIndexedColumn> getIndexedColumnList();

  @Nullable
  PsiElement getCheck();

  @Nullable
  PsiElement getConstraint();

  @Nullable
  PsiElement getForeign();

  @Nullable
  PsiElement getKey();

  @Nullable
  PsiElement getPrimary();

  @Nullable
  PsiElement getUnique();

}
