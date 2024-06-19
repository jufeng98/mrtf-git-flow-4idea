// This is a generated file. Not intended for manual editing.
package com.github.xiaolyuh.pcel.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;

public class AopVisitor extends PsiElementVisitor {

  public void visitContent(@NotNull AopContent o) {
    visitPsiElement(o);
  }

  public void visitExpr(@NotNull AopExpr o) {
    visitPsiElement(o);
  }

  public void visitKind(@NotNull AopKind o) {
    visitPsiElement(o);
  }

  public void visitMethod(@NotNull AopMethod o) {
    visitPsiElement(o);
  }

  public void visitPointcut(@NotNull AopPointcut o) {
    visitPsiElement(o);
  }

  public void visitPointcutCombination(@NotNull AopPointcutCombination o) {
    visitPsiElement(o);
  }

  public void visitValue(@NotNull AopValue o) {
    visitPsiElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
