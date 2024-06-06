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
  // (ALPHA | '.')*
  public static boolean expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr")) return false;
    Marker m = enter_section_(b, l, _NONE_, EXPR, "<expr>");
    while (true) {
      int c = current_position_(b);
      if (!expr_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "expr", c)) break;
    }
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  // ALPHA | '.'
  private static boolean expr_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr_0")) return false;
    boolean r;
    r = consumeToken(b, ALPHA);
    if (!r) r = consumeToken(b, ".");
    return r;
  }

  /* ********************************************************** */
  // kind LP expr RP
  static boolean item_(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "item_")) return false;
    if (!nextTokenIs(b, "", ANNOTATION, TARGET)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = kind(b, l + 1);
    p = r; // pin = 1
    r = r && report_error_(b, consumeToken(b, LP));
    r = p && report_error_(b, expr(b, l + 1)) && r;
    r = p && consumeToken(b, RP) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // ANNOTATION | TARGET
  public static boolean kind(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "kind")) return false;
    if (!nextTokenIs(b, "<kind>", ANNOTATION, TARGET)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, KIND, "<kind>");
    r = consumeToken(b, ANNOTATION);
    if (!r) r = consumeToken(b, TARGET);
    exit_section_(b, l, m, r, false, null);
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

}
