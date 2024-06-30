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

public class SqlColumnDefImpl extends SqlCompositeElementImpl implements SqlColumnDef {

  public SqlColumnDefImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SqlVisitor visitor) {
    visitor.visitColumnDef(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SqlVisitor) accept((SqlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<SqlColumnConstraint> getColumnConstraintList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SqlColumnConstraint.class);
  }

  @Override
  @NotNull
  public SqlColumnName getColumnName() {
    return findNotNullChildByClass(SqlColumnName.class);
  }

  @Override
  @NotNull
  public SqlTypeName getTypeName() {
    return findNotNullChildByClass(SqlTypeName.class);
  }

}
