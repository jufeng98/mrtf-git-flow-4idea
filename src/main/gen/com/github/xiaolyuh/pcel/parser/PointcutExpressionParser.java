// This is a generated file. Not intended for manual editing.
package com.github.xiaolyuh.pcel.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static com.github.xiaolyuh.pcel.psi.PointcutExpressionTypes.*;
import static com.github.xiaolyuh.pcel.parser.PointcutExpressionParserUtil.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class PointcutExpressionParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType t, PsiBuilder b) {
    parseLight(t, b);
    return b.getTreeBuilt();
  }

  public void parseLight(IElementType t, PsiBuilder b) {
    boolean r;
    b = adapt_builder_(t, b, this, null);
    Marker m = enter_section_(b, 0, _COLLAPSE_, null);
    r = parse_root_(t, b);
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType t, PsiBuilder b) {
    return parse_root_(t, b, 0);
  }

  static boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return pointcutExpressionFile(b, l + 1);
  }

  /* ********************************************************** */
  // EXPR
  public static boolean aop_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "aop_expr")) return false;
    if (!nextTokenIs(b, EXPR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EXPR);
    exit_section_(b, m, AOP_EXPR, r);
    return r;
  }

  /* ********************************************************** */
  // ANNOTATION | ANNO_TARGET | EXECUTION | BEAN
  public static boolean aop_kind(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "aop_kind")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, AOP_KIND, "<aop kind>");
    r = consumeToken(b, ANNOTATION);
    if (!r) r = consumeToken(b, ANNO_TARGET);
    if (!r) r = consumeToken(b, EXECUTION);
    if (!r) r = consumeToken(b, BEAN);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // aop_kind aop_expr | reference
  static boolean item_(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "item_")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = item__0(b, l + 1);
    if (!r) r = reference(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // aop_kind aop_expr
  private static boolean item__0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "item__0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = aop_kind(b, l + 1);
    r = r && aop_expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // item_*
  static boolean pointcutExpressionFile(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pointcutExpressionFile")) return false;
    while (true) {
      int c = current_position_(b);
      if (!item_(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "pointcutExpressionFile", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // METHOD_REFERENCE
  public static boolean reference(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reference")) return false;
    if (!nextTokenIs(b, METHOD_REFERENCE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, METHOD_REFERENCE);
    exit_section_(b, m, REFERENCE, r);
    return r;
  }

}
