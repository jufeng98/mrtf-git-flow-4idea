// This is a generated file. Not intended for manual editing.
package com.github.xiaolyuh.sql.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.alecstrong.sql.psi.core.psi.SqlCompositeElement;

public interface SqlBeginStmt extends SqlCompositeElement {

  @NotNull
  PsiElement getBegin();

  @Nullable
  PsiElement getDeferred();

  @Nullable
  PsiElement getExclusive();

  @Nullable
  PsiElement getImmediate();

  @Nullable
  PsiElement getTransaction();

}
