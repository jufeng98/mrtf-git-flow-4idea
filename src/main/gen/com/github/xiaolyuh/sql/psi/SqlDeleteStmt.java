// This is a generated file. Not intended for manual editing.
package com.github.xiaolyuh.sql.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.alecstrong.sql.psi.core.psi.SqlCompositeElement;

public interface SqlDeleteStmt extends SqlCompositeElement {

  @Nullable
  SqlExpr getExpr();

  @NotNull
  SqlQualifiedTableName getQualifiedTableName();

  @Nullable
  SqlWithClause getWithClause();

  @NotNull
  PsiElement getDelete();

  @NotNull
  PsiElement getFrom();

  @Nullable
  PsiElement getWhere();

}
