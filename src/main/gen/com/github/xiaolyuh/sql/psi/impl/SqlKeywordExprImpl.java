// This is a generated file. Not intended for manual editing.
package com.github.xiaolyuh.sql.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.github.xiaolyuh.sql.psi.SqlTypes.*;
import com.github.xiaolyuh.sql.psi.*;

public class SqlKeywordExprImpl extends SqlExprImpl implements SqlKeywordExpr {

  public SqlKeywordExprImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull SqlVisitor visitor) {
    visitor.visitKeywordExpr(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SqlVisitor) accept((SqlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public PsiElement getDay() {
    return findChildByType(DAY);
  }

  @Override
  @Nullable
  public PsiElement getHour() {
    return findChildByType(HOUR);
  }

  @Override
  @Nullable
  public PsiElement getMonth() {
    return findChildByType(MONTH);
  }

  @Override
  @Nullable
  public PsiElement getSeparator() {
    return findChildByType(SEPARATOR);
  }

}
