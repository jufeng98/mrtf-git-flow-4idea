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

public class SqlPragmaValueImpl extends SqlPsiElement implements SqlPragmaValue {

  public SqlPragmaValueImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SqlVisitor visitor) {
    visitor.visitPragmaValue(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SqlVisitor) accept((SqlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public SqlIdentifier getIdentifier() {
    return findChildByClass(SqlIdentifier.class);
  }

  @Override
  @Nullable
  public SqlSignedNumber getSignedNumber() {
    return findChildByClass(SqlSignedNumber.class);
  }

  @Override
  @Nullable
  public SqlStringLiteral getStringLiteral() {
    return findChildByClass(SqlStringLiteral.class);
  }

}
