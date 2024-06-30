// This is a generated file. Not intended for manual editing.
package com.github.xiaolyuh.sql.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.alecstrong.sql.psi.core.psi.QueryElement;
import com.alecstrong.sql.psi.core.psi.SqlCompositeElement;

public interface SqlResultColumn extends QueryElement, SqlCompositeElement {

  @Nullable
  SqlColumnAlias getColumnAlias();

  @Nullable
  SqlExpr getExpr();

  @Nullable
  SqlTableName getTableName();

  @Nullable
  PsiElement getAs();

}
