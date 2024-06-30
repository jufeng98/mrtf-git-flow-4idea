// This is a generated file. Not intended for manual editing.
package com.github.xiaolyuh.sql.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.alecstrong.sql.psi.core.psi.SqlCompositeElement;

public interface SqlDropViewStmt extends SqlCompositeElement {

  @Nullable
  SqlDatabaseName getDatabaseName();

  @Nullable
  SqlViewName getViewName();

  @NotNull
  PsiElement getDrop();

  @Nullable
  PsiElement getExists();

  @Nullable
  PsiElement getIf();

  @NotNull
  PsiElement getView();

}
