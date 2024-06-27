// This is a generated file. Not intended for manual editing.
package com.github.xiaolyuh.spel.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.github.xiaolyuh.spel.psi.SpelTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.github.xiaolyuh.spel.psi.*;
import com.intellij.psi.PsiReference;

public class SpelRootImpl extends ASTWrapperPsiElement implements SpelRoot {

  public SpelRootImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SpelVisitor visitor) {
    visitor.visitRoot(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SpelVisitor) accept((SpelVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<SpelCollectionProjection> getCollectionProjectionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SpelCollectionProjection.class);
  }

  @Override
  @NotNull
  public List<SpelCollectionSelection> getCollectionSelectionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SpelCollectionSelection.class);
  }

  @Override
  @NotNull
  public List<SpelFieldOrMethod> getFieldOrMethodList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SpelFieldOrMethod.class);
  }

  @Override
  @Nullable
  public SpelFieldOrMethodName getFieldOrMethodName() {
    return findChildByClass(SpelFieldOrMethodName.class);
  }

  @Override
  @NotNull
  public List<SpelMapSelection> getMapSelectionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SpelMapSelection.class);
  }

  @Override
  @NotNull
  public List<SpelMethodCall> getMethodCallList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SpelMethodCall.class);
  }

  @Override
  @Nullable
  public SpelNumberLiteral getNumberLiteral() {
    return findChildByClass(SpelNumberLiteral.class);
  }

  @Override
  @Nullable
  public SpelStringLiteral getStringLiteral() {
    return findChildByClass(SpelStringLiteral.class);
  }

  @Override
  public PsiReference @NotNull [] getReferences() {
    return SpelPsiImplUtil.getReferences(this);
  }

}
