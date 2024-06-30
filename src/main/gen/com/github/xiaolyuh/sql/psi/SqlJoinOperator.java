// This is a generated file. Not intended for manual editing.
package com.github.xiaolyuh.sql.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.alecstrong.sql.psi.core.psi.SqlCompositeElement;

public interface SqlJoinOperator extends SqlCompositeElement {

  @Nullable
  PsiElement getCross();

  @Nullable
  PsiElement getInner();

  @Nullable
  PsiElement getJoin();

  @Nullable
  PsiElement getLeft();

  @Nullable
  PsiElement getNatural();

  @Nullable
  PsiElement getOuter();

}
