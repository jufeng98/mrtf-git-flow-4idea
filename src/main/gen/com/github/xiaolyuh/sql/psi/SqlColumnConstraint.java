// This is a generated file. Not intended for manual editing.
package com.github.xiaolyuh.sql.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.alecstrong.sql.psi.core.psi.SqlCompositeElement;

public interface SqlColumnConstraint extends SqlCompositeElement {

  @Nullable
  SqlCollationName getCollationName();

  @Nullable
  SqlConflictClause getConflictClause();

  @Nullable
  SqlExpr getExpr();

  @Nullable
  SqlForeignKeyClause getForeignKeyClause();

  @Nullable
  SqlIdentifier getIdentifier();

  @Nullable
  SqlLiteralValue getLiteralValue();

  @Nullable
  SqlSignedNumber getSignedNumber();

  @Nullable
  PsiElement getAsc();

  @Nullable
  PsiElement getAutoincrement();

  @Nullable
  PsiElement getCheck();

  @Nullable
  PsiElement getCollate();

  @Nullable
  PsiElement getConstraint();

  @Nullable
  PsiElement getDefault();

  @Nullable
  PsiElement getDesc();

  @Nullable
  PsiElement getKey();

  @Nullable
  PsiElement getNot();

  @Nullable
  PsiElement getNull();

  @Nullable
  PsiElement getPrimary();

  @Nullable
  PsiElement getUnique();

}
