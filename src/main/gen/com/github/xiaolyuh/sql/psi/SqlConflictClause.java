// This is a generated file. Not intended for manual editing.
package com.github.xiaolyuh.sql.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SqlConflictClause extends PsiElement {

  @Nullable
  PsiElement getAbort();

  @Nullable
  PsiElement getFail();

  @Nullable
  PsiElement getIgnore();

  @Nullable
  PsiElement getOn();

  @Nullable
  PsiElement getRollback();

}
