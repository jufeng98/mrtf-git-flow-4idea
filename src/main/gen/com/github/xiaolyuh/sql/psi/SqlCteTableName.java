// This is a generated file. Not intended for manual editing.
package com.github.xiaolyuh.sql.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.alecstrong.sql.psi.core.psi.SqlCompositeElement;

public interface SqlCteTableName extends SqlCompositeElement {

  @NotNull
  List<SqlColumnAlias> getColumnAliasList();

  @NotNull
  SqlTableName getTableName();

}
