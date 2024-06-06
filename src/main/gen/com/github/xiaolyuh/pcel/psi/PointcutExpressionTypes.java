// This is a generated file. Not intended for manual editing.
package com.github.xiaolyuh.pcel.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import com.github.xiaolyuh.pcel.psi.impl.*;

public interface PointcutExpressionTypes {

  IElementType EXPR = new PointcutExpressionElementType("EXPR");
  IElementType KIND = new PointcutExpressionElementType("KIND");

  IElementType ALPHA = new PointcutExpressionTokenType("ALPHA");
  IElementType ANNOTATION = new PointcutExpressionTokenType("@annotation");
  IElementType LP = new PointcutExpressionTokenType("(");
  IElementType RP = new PointcutExpressionTokenType(")");
  IElementType TARGET = new PointcutExpressionTokenType("@target");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == EXPR) {
        return new PointcutExpressionExprImpl(node);
      }
      else if (type == KIND) {
        return new PointcutExpressionKindImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
