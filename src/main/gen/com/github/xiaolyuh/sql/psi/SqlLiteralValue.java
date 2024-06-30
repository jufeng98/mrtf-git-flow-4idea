// This is a generated file. Not intended for manual editing.
package com.github.xiaolyuh.sql.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.alecstrong.sql.psi.core.psi.SqlCompositeElement;

public interface SqlLiteralValue extends SqlCompositeElement {

  @Nullable
  SqlBlobLiteral getBlobLiteral();

  @Nullable
  SqlNumericLiteral getNumericLiteral();

  @Nullable
  SqlStringLiteral getStringLiteral();

  @Nullable
  PsiElement getCurrentDate();

  @Nullable
  PsiElement getCurrentTime();

  @Nullable
  PsiElement getCurrentTimestamp();

  @Nullable
  PsiElement getNull();

}
