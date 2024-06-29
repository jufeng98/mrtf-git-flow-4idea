// This is a generated file. Not intended for manual editing.
package com.github.xiaolyuh.spel.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import com.github.xiaolyuh.spel.psi.impl.*;

public interface SpelTypes {

  IElementType ARRAY_WRAP = new SpelElementType("ARRAY_WRAP");
  IElementType COLLECTION_PROJECTION = new SpelElementType("COLLECTION_PROJECTION");
  IElementType COLLECTION_SELECTION = new SpelElementType("COLLECTION_SELECTION");
  IElementType FIELD_NAME = new SpelElementType("FIELD_NAME");
  IElementType FIELD_OR_METHOD = new SpelElementType("FIELD_OR_METHOD");
  IElementType FIELD_OR_METHOD_NAME = new SpelElementType("FIELD_OR_METHOD_NAME");
  IElementType FIELD_RECURSIVE_CALL = new SpelElementType("FIELD_RECURSIVE_CALL");
  IElementType MAP_SELECTION = new SpelElementType("MAP_SELECTION");
  IElementType METHOD_CALL = new SpelElementType("METHOD_CALL");
  IElementType METHOD_PARAM = new SpelElementType("METHOD_PARAM");
  IElementType METHOD_PARAMS = new SpelElementType("METHOD_PARAMS");
  IElementType NUMBER_LITERAL = new SpelElementType("NUMBER_LITERAL");
  IElementType ROOT = new SpelElementType("ROOT");
  IElementType ROOT_COMBINATION = new SpelElementType("ROOT_COMBINATION");
  IElementType SELECTION_EXPRESSION = new SpelElementType("SELECTION_EXPRESSION");
  IElementType SPEL = new SpelElementType("SPEL");
  IElementType STATIC_T = new SpelElementType("STATIC_T");
  IElementType STRING_LITERAL = new SpelElementType("STRING_LITERAL");

  IElementType COMMA = new SpelTokenType("COMMA");
  IElementType DOT = new SpelTokenType(".");
  IElementType EXPR = new SpelTokenType("EXPR");
  IElementType IDENTIFIER = new SpelTokenType("IDENTIFIER");
  IElementType L_BRACE = new SpelTokenType("{");
  IElementType L_BRACKET = new SpelTokenType("[");
  IElementType L_PARENTHESES = new SpelTokenType("(");
  IElementType NUMBER = new SpelTokenType("NUMBER");
  IElementType PLUS = new SpelTokenType("PLUS");
  IElementType PROJECTION = new SpelTokenType(".![");
  IElementType R_BRACE = new SpelTokenType("}");
  IElementType R_BRACKET = new SpelTokenType("]");
  IElementType R_PARENTHESES = new SpelTokenType(")");
  IElementType SELECTION = new SpelTokenType(".?[");
  IElementType SHARP = new SpelTokenType("#");
  IElementType SINGLE_QUOTED_STRING = new SpelTokenType("SINGLE_QUOTED_STRING");
  IElementType STATIC_REFERENCE = new SpelTokenType("STATIC_REFERENCE");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == ARRAY_WRAP) {
        return new SpelArrayWrapImpl(node);
      }
      else if (type == COLLECTION_PROJECTION) {
        return new SpelCollectionProjectionImpl(node);
      }
      else if (type == COLLECTION_SELECTION) {
        return new SpelCollectionSelectionImpl(node);
      }
      else if (type == FIELD_NAME) {
        return new SpelFieldNameImpl(node);
      }
      else if (type == FIELD_OR_METHOD) {
        return new SpelFieldOrMethodImpl(node);
      }
      else if (type == FIELD_OR_METHOD_NAME) {
        return new SpelFieldOrMethodNameImpl(node);
      }
      else if (type == FIELD_RECURSIVE_CALL) {
        return new SpelFieldRecursiveCallImpl(node);
      }
      else if (type == MAP_SELECTION) {
        return new SpelMapSelectionImpl(node);
      }
      else if (type == METHOD_CALL) {
        return new SpelMethodCallImpl(node);
      }
      else if (type == METHOD_PARAM) {
        return new SpelMethodParamImpl(node);
      }
      else if (type == METHOD_PARAMS) {
        return new SpelMethodParamsImpl(node);
      }
      else if (type == NUMBER_LITERAL) {
        return new SpelNumberLiteralImpl(node);
      }
      else if (type == ROOT) {
        return new SpelRootImpl(node);
      }
      else if (type == ROOT_COMBINATION) {
        return new SpelRootCombinationImpl(node);
      }
      else if (type == SELECTION_EXPRESSION) {
        return new SpelSelectionExpressionImpl(node);
      }
      else if (type == SPEL) {
        return new SpelSpelImpl(node);
      }
      else if (type == STATIC_T) {
        return new SpelStaticTImpl(node);
      }
      else if (type == STRING_LITERAL) {
        return new SpelStringLiteralImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
