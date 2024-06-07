// This is a generated file. Not intended for manual editing.
package com.github.xiaolyuh.pcel.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import com.github.xiaolyuh.pcel.psi.impl.*;

public interface PointcutExpressionTypes {

  IElementType AOP_EXPR = new PointcutExpressionElementType("AOP_EXPR");
  IElementType AOP_KIND = new PointcutExpressionElementType("AOP_KIND");
  IElementType REFERENCE = new PointcutExpressionElementType("REFERENCE");

  IElementType ANNOTATION = new PointcutExpressionTokenType("@annotation");
  IElementType ANNO_TARGET = new PointcutExpressionTokenType("@target");
  IElementType BEAN = new PointcutExpressionTokenType("bean");
  IElementType EXECUTION = new PointcutExpressionTokenType("execution");
  IElementType EXPR = new PointcutExpressionTokenType("EXPR");
  IElementType METHOD_REFERENCE = new PointcutExpressionTokenType("METHOD_REFERENCE");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == AOP_EXPR) {
        return new PointcutExpressionAopExprImpl(node);
      }
      else if (type == AOP_KIND) {
        return new PointcutExpressionAopKindImpl(node);
      }
      else if (type == REFERENCE) {
        return new PointcutExpressionReferenceImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
