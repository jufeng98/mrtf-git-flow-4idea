// This is a generated file. Not intended for manual editing.
package com.github.xiaolyuh.pcel.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import com.github.xiaolyuh.pcel.psi.impl.*;

public interface PointcutExpressionTypes {

  IElementType CONTENT = new PointcutExpressionElementType("CONTENT");
  IElementType EXPR = new PointcutExpressionElementType("EXPR");
  IElementType KIND = new PointcutExpressionElementType("KIND");
  IElementType METHOD = new PointcutExpressionElementType("METHOD");
  IElementType POINTCUT = new PointcutExpressionElementType("POINTCUT");
  IElementType POINTCUT_COMBINATION = new PointcutExpressionElementType("POINTCUT_COMBINATION");
  IElementType VALUE = new PointcutExpressionElementType("VALUE");

  IElementType AND_OPERATOR = new PointcutExpressionTokenType("AND_OPERATOR");
  IElementType AT_ANNOTATION = new PointcutExpressionTokenType("@annotation");
  IElementType AT_TARGET = new PointcutExpressionTokenType("@target");
  IElementType BEAN = new PointcutExpressionTokenType("bean");
  IElementType EXECUTION = new PointcutExpressionTokenType("execution");
  IElementType EXPR_PATTERN = new PointcutExpressionTokenType("EXPR_PATTERN");
  IElementType METHOD_REFERENCE = new PointcutExpressionTokenType("METHOD_REFERENCE");
  IElementType OR_OPERATOR = new PointcutExpressionTokenType("OR_OPERATOR");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == CONTENT) {
        return new AopContentImpl(node);
      }
      else if (type == EXPR) {
        return new AopExprImpl(node);
      }
      else if (type == KIND) {
        return new AopKindImpl(node);
      }
      else if (type == METHOD) {
        return new AopMethodImpl(node);
      }
      else if (type == POINTCUT) {
        return new AopPointcutImpl(node);
      }
      else if (type == POINTCUT_COMBINATION) {
        return new AopPointcutCombinationImpl(node);
      }
      else if (type == VALUE) {
        return new AopValueImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
