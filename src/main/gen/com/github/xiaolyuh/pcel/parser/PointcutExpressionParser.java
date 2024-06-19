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
  // value | method
  public static boolean content(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "content")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CONTENT, "<content>");
    r = value(b, l + 1);
    if (!r) r = method(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // EXPR_PATTERN
  public static boolean expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr")) return false;
    if (!nextTokenIs(b, EXPR_PATTERN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EXPR_PATTERN);
    exit_section_(b, m, EXPR, r);
    return r;
  }

  /* ********************************************************** */
  // AT_ANNOTATION | AT_TARGET | EXECUTION | BEAN
  public static boolean kind(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "kind")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, KIND, "<kind>");
    r = consumeToken(b, AT_ANNOTATION);
    if (!r) r = consumeToken(b, AT_TARGET);
    if (!r) r = consumeToken(b, EXECUTION);
    if (!r) r = consumeToken(b, BEAN);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // METHOD_REFERENCE
  public static boolean method(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "method")) return false;
    if (!nextTokenIs(b, METHOD_REFERENCE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, METHOD_REFERENCE);
    exit_section_(b, m, METHOD, r);
    return r;
  }

  /* ********************************************************** */
  // content pointcut_combination?
  public static boolean pointcut(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pointcut")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, POINTCUT, "<pointcut>");
    r = content(b, l + 1);
    p = r; // pin = 1
    r = r && pointcut_1(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // pointcut_combination?
  private static boolean pointcut_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pointcut_1")) return false;
    pointcut_combination(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // pointcut*
  static boolean pointcutExpressionFile(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pointcutExpressionFile")) return false;
    while (true) {
      int c = current_position_(b);
      if (!pointcut(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "pointcutExpressionFile", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // (OR_OPERATOR | AND_OPERATOR) pointcut
  public static boolean pointcut_combination(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pointcut_combination")) return false;
    if (!nextTokenIs(b, "<pointcut combination>", AND_OPERATOR, OR_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, POINTCUT_COMBINATION, "<pointcut combination>");
    r = pointcut_combination_0(b, l + 1);
    r = r && pointcut(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // OR_OPERATOR | AND_OPERATOR
  private static boolean pointcut_combination_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pointcut_combination_0")) return false;
    boolean r;
    r = consumeToken(b, OR_OPERATOR);
    if (!r) r = consumeToken(b, AND_OPERATOR);
    return r;
  }

  /* ********************************************************** */
  // kind expr
  public static boolean value(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "value")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, VALUE, "<value>");
    r = kind(b, l + 1);
    r = r && expr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

}
