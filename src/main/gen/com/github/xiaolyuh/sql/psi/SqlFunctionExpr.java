// This is a generated file. Not intended for manual editing.
package com.github.xiaolyuh.sql.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SqlFunctionExpr extends SqlExpr {

  @NotNull
  List<SqlExpr> getExprList();

  @Nullable
  SqlFunctionName getFunctionName();

  @Nullable
  PsiElement getDistinct();

  @Nullable
  PsiElement getIf();

}
