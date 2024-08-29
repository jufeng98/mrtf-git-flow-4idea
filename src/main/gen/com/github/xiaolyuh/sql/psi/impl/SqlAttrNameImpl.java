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

public class SqlAttrNameImpl extends SqlPsiElement implements SqlAttrName {

  public SqlAttrNameImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SqlVisitor visitor) {
    visitor.visitAttrName(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SqlVisitor) accept((SqlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public PsiElement getAutoIncrement() {
    return findChildByType(AUTO_INCREMENT);
  }

  @Override
  @Nullable
  public PsiElement getCollate() {
    return findChildByType(COLLATE);
  }

  @Override
  @Nullable
  public PsiElement getCommentWord() {
    return findChildByType(COMMENT_WORD);
  }

  @Override
  @Nullable
  public PsiElement getDefault() {
    return findChildByType(DEFAULT);
  }

  @Override
  @Nullable
  public PsiElement getId() {
    return findChildByType(ID);
  }

}
