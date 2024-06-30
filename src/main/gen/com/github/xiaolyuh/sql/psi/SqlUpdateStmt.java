// This is a generated file. Not intended for manual editing.
package com.github.xiaolyuh.sql.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.alecstrong.sql.psi.core.psi.SqlCompositeElement;

public interface SqlUpdateStmt extends SqlCompositeElement {

  @Nullable
  SqlColumnName getColumnName();

  @Nullable
  SqlExpr getExpr();

  @NotNull
  SqlQualifiedTableName getQualifiedTableName();

  @Nullable
  SqlSetterExpression getSetterExpression();

  @NotNull
  List<SqlUpdateStmtSubsequentSetter> getUpdateStmtSubsequentSetterList();

  @Nullable
  SqlWithClause getWithClause();

  @Nullable
  PsiElement getRollback();

  @Nullable
  PsiElement getSet();

  @NotNull
  PsiElement getUpdate();

  @Nullable
  PsiElement getWhere();

}
