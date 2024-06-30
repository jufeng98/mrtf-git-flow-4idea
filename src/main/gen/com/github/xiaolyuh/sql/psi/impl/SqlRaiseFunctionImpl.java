// This is a generated file. Not intended for manual editing.
package com.github.xiaolyuh.sql.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.github.xiaolyuh.sql.psi.SqlTypes.*;
import com.alecstrong.sql.psi.core.psi.SqlCompositeElementImpl;
import com.github.xiaolyuh.sql.psi.*;

public class SqlRaiseFunctionImpl extends SqlCompositeElementImpl implements SqlRaiseFunction {

  public SqlRaiseFunctionImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SqlVisitor visitor) {
    visitor.visitRaiseFunction(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SqlVisitor) accept((SqlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public SqlErrorMessage getErrorMessage() {
    return findChildByClass(SqlErrorMessage.class);
  }

  @Override
  @Nullable
  public PsiElement getAbort() {
    return findChildByType(ABORT);
  }

  @Override
  @Nullable
  public PsiElement getFail() {
    return findChildByType(FAIL);
  }

  @Override
  @Nullable
  public PsiElement getIgnore() {
    return findChildByType(IGNORE);
  }

  @Override
  @NotNull
  public PsiElement getRaise() {
    return findNotNullChildByType(RAISE);
  }

  @Override
  @Nullable
  public PsiElement getRollback() {
    return findChildByType(ROLLBACK);
  }

}
