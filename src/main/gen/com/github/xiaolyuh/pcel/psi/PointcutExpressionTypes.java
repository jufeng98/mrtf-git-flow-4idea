// This is a generated file. Not intended for manual editing.
package com.github.xiaolyuh.pcel.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import com.github.xiaolyuh.pcel.psi.impl.*;

public interface PointcutExpressionTypes {

  IElementType AOP_CONTENT = new PointcutExpressionElementType("AOP_CONTENT");
  IElementType AOP_EXPR = new PointcutExpressionElementType("AOP_EXPR");
  IElementType AOP_KIND = new PointcutExpressionElementType("AOP_KIND");
  IElementType AOP_METHOD_REFERENCE = new PointcutExpressionElementType("AOP_METHOD_REFERENCE");
  IElementType AOP_PARALLEL_POINTCUT = new PointcutExpressionElementType("AOP_PARALLEL_POINTCUT");
  IElementType AOP_POINTCUT = new PointcutExpressionElementType("AOP_POINTCUT");

  IElementType AND_OPERATOR = new PointcutExpressionTokenType("AND_OPERATOR");
  IElementType ANNOTATION = new PointcutExpressionTokenType("@annotation");
  IElementType ANNO_TARGET = new PointcutExpressionTokenType("@target");
  IElementType BEAN = new PointcutExpressionTokenType("bean");
  IElementType EXECUTION = new PointcutExpressionTokenType("execution");
  IElementType EXPR = new PointcutExpressionTokenType("EXPR");
  IElementType METHOD_REFERENCE = new PointcutExpressionTokenType("METHOD_REFERENCE");
  IElementType OR_OPERATOR = new PointcutExpressionTokenType("OR_OPERATOR");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == AOP_CONTENT) {
        return new PointcutExpressionAopContentImpl(node);
      }
      else if (type == AOP_EXPR) {
        return new PointcutExpressionAopExprImpl(node);
      }
      else if (type == AOP_KIND) {
        return new PointcutExpressionAopKindImpl(node);
      }
      else if (type == AOP_METHOD_REFERENCE) {
        return new PointcutExpressionAopMethodReferenceImpl(node);
      }
      else if (type == AOP_PARALLEL_POINTCUT) {
        return new PointcutExpressionAopParallelPointcutImpl(node);
      }
      else if (type == AOP_POINTCUT) {
        return new PointcutExpressionAopPointcutImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
