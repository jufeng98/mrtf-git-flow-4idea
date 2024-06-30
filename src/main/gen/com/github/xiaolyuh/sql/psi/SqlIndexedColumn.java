// This is a generated file. Not intended for manual editing.
package com.github.xiaolyuh.sql.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.alecstrong.sql.psi.core.psi.SqlCompositeElement;

public interface SqlIndexedColumn extends SqlCompositeElement {

  @Nullable
  SqlCollationName getCollationName();

  @NotNull
  SqlColumnName getColumnName();

  @Nullable
  PsiElement getAsc();

  @Nullable
  PsiElement getCollate();

  @Nullable
  PsiElement getDesc();

}
