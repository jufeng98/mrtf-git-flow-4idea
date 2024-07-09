// This is a generated file. Not intended for manual editing.
package com.github.xiaolyuh.sql.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SqlColumnConstraint extends PsiElement {

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
  PsiElement getAutoIncrement();

  @Nullable
  PsiElement getCheck();

  @Nullable
  PsiElement getCollate();

  @Nullable
  PsiElement getComment();

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

  @Nullable
  PsiElement getUnsigned();

  @Nullable
  PsiElement getString();

}
