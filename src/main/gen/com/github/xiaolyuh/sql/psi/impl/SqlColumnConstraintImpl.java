// This is a generated file. Not intended for manual editing.
package com.github.xiaolyuh.sql.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.github.xiaolyuh.sql.psi.SqlTypes.*;
import com.github.xiaolyuh.sql.SqlPsiElement;
import com.github.xiaolyuh.sql.psi.*;

public class SqlColumnConstraintImpl extends SqlPsiElement implements SqlColumnConstraint {

  public SqlColumnConstraintImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SqlVisitor visitor) {
    visitor.visitColumnConstraint(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SqlVisitor) accept((SqlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public SqlCollationName getCollationName() {
    return findChildByClass(SqlCollationName.class);
  }

  @Override
  @Nullable
  public SqlConflictClause getConflictClause() {
    return findChildByClass(SqlConflictClause.class);
  }

  @Override
  @Nullable
  public SqlExpr getExpr() {
    return findChildByClass(SqlExpr.class);
  }

  @Override
  @Nullable
  public SqlForeignKeyClause getForeignKeyClause() {
    return findChildByClass(SqlForeignKeyClause.class);
  }

  @Override
  @Nullable
  public SqlIdentifier getIdentifier() {
    return findChildByClass(SqlIdentifier.class);
  }

  @Override
  @Nullable
  public SqlLiteralValue getLiteralValue() {
    return findChildByClass(SqlLiteralValue.class);
  }

  @Override
  @Nullable
  public SqlSignedNumber getSignedNumber() {
    return findChildByClass(SqlSignedNumber.class);
  }

  @Override
  @Nullable
  public PsiElement getAsc() {
    return findChildByType(ASC);
  }

  @Override
  @Nullable
  public PsiElement getAutoincrement() {
    return findChildByType(AUTOINCREMENT);
  }

  @Override
  @Nullable
  public PsiElement getAutoIncrement() {
    return findChildByType(AUTO_INCREMENT);
  }

  @Override
  @Nullable
  public PsiElement getCheck() {
    return findChildByType(CHECK);
  }

  @Override
  @Nullable
  public PsiElement getCollate() {
    return findChildByType(COLLATE);
  }

  @Override
  @Nullable
  public PsiElement getComment() {
    return findChildByType(COMMENT);
  }

  @Override
  @Nullable
  public PsiElement getConstraint() {
    return findChildByType(CONSTRAINT);
  }

  @Override
  @Nullable
  public PsiElement getDefault() {
    return findChildByType(DEFAULT);
  }

  @Override
  @Nullable
  public PsiElement getDesc() {
    return findChildByType(DESC);
  }

  @Override
  @Nullable
  public PsiElement getKey() {
    return findChildByType(KEY);
  }

  @Override
  @Nullable
  public PsiElement getNot() {
    return findChildByType(NOT);
  }

  @Override
  @Nullable
  public PsiElement getNull() {
    return findChildByType(NULL);
  }

  @Override
  @Nullable
  public PsiElement getPrimary() {
    return findChildByType(PRIMARY);
  }

  @Override
  @Nullable
  public PsiElement getUnique() {
    return findChildByType(UNIQUE);
  }

  @Override
  @Nullable
  public PsiElement getUnsigned() {
    return findChildByType(UNSIGNED);
  }

  @Override
  @Nullable
  public PsiElement getString() {
    return findChildByType(STRING);
  }

}
