// This is a generated file. Not intended for manual editing.
package com.github.xiaolyuh.spel.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static com.github.xiaolyuh.spel.psi.SpelTypes.*;
import static com.github.xiaolyuh.spel.parser.SpelParserUtil.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class SpelParser implements PsiParser, LightPsiParser {

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
    return spelFile(b, l + 1);
  }

  /* ********************************************************** */
  // PROJECTION field_recursive_call R_BRACKET
  public static boolean collection_projection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "collection_projection")) return false;
    if (!nextTokenIs(b, PROJECTION)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, PROJECTION);
    r = r && field_recursive_call(b, l + 1);
    r = r && consumeToken(b, R_BRACKET);
    exit_section_(b, m, COLLECTION_PROJECTION, r);
    return r;
  }

  /* ********************************************************** */
  // SELECTION selection_expression R_BRACKET
  public static boolean collection_selection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "collection_selection")) return false;
    if (!nextTokenIs(b, SELECTION)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, SELECTION);
    r = r && selection_expression(b, l + 1);
    r = r && consumeToken(b, R_BRACKET);
    exit_section_(b, m, COLLECTION_SELECTION, r);
    return r;
  }

  /* ********************************************************** */
  // IDENTIFIER
  public static boolean field_name(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "field_name")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IDENTIFIER);
    exit_section_(b, m, FIELD_NAME, r);
    return r;
  }

  /* ********************************************************** */
  // DOT field_or_method_name method_call?
  public static boolean field_or_method(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "field_or_method")) return false;
    if (!nextTokenIs(b, DOT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, DOT);
    r = r && field_or_method_name(b, l + 1);
    r = r && field_or_method_2(b, l + 1);
    exit_section_(b, m, FIELD_OR_METHOD, r);
    return r;
  }

  // method_call?
  private static boolean field_or_method_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "field_or_method_2")) return false;
    method_call(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // IDENTIFIER
  public static boolean field_or_method_name(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "field_or_method_name")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IDENTIFIER);
    exit_section_(b, m, FIELD_OR_METHOD_NAME, r);
    return r;
  }

  /* ********************************************************** */
  // SHARP? field_name field_or_method*
  public static boolean field_recursive_call(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "field_recursive_call")) return false;
    if (!nextTokenIs(b, "<field recursive call>", IDENTIFIER, SHARP)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FIELD_RECURSIVE_CALL, "<field recursive call>");
    r = field_recursive_call_0(b, l + 1);
    r = r && field_name(b, l + 1);
    r = r && field_recursive_call_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // SHARP?
  private static boolean field_recursive_call_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "field_recursive_call_0")) return false;
    consumeToken(b, SHARP);
    return true;
  }

  // field_or_method*
  private static boolean field_recursive_call_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "field_recursive_call_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!field_or_method(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "field_recursive_call_2", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // spel (PLUS spel)*
  static boolean item_(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "item_")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = spel(b, l + 1);
    r = r && item__1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (PLUS spel)*
  private static boolean item__1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "item__1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!item__1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "item__1", c)) break;
    }
    return true;
  }

  // PLUS spel
  private static boolean item__1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "item__1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, PLUS);
    r = r && spel(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // L_BRACKET field_name R_BRACKET
  public static boolean map_selection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "map_selection")) return false;
    if (!nextTokenIs(b, L_BRACKET)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, L_BRACKET);
    r = r && field_name(b, l + 1);
    r = r && consumeToken(b, R_BRACKET);
    exit_section_(b, m, MAP_SELECTION, r);
    return r;
  }

  /* ********************************************************** */
  // '(' method_params? ')'
  public static boolean method_call(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "method_call")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, METHOD_CALL, "<method call>");
    r = consumeToken(b, "(");
    p = r; // pin = 1
    r = r && report_error_(b, method_call_1(b, l + 1));
    r = p && consumeToken(b, ")") && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // method_params?
  private static boolean method_call_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "method_call_1")) return false;
    method_params(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // spel
  public static boolean method_param(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "method_param")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, METHOD_PARAM, "<method param>");
    r = spel(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // method_param (COMMA method_param)*
  public static boolean method_params(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "method_params")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, METHOD_PARAMS, "<method params>");
    r = method_param(b, l + 1);
    r = r && method_params_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (COMMA method_param)*
  private static boolean method_params_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "method_params_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!method_params_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "method_params_1", c)) break;
    }
    return true;
  }

  // COMMA method_param
  private static boolean method_params_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "method_params_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && method_param(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // NUMBER
  public static boolean number_literal(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "number_literal")) return false;
    if (!nextTokenIs(b, NUMBER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, NUMBER);
    exit_section_(b, m, NUMBER_LITERAL, r);
    return r;
  }

  /* ********************************************************** */
  // EXPR
  public static boolean selection_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "selection_expression")) return false;
    if (!nextTokenIs(b, EXPR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EXPR);
    exit_section_(b, m, SELECTION_EXPRESSION, r);
    return r;
  }

  /* ********************************************************** */
  // (string_literal | number_literal | SHARP field_or_method_name) (field_or_method | method_call | map_selection | collection_projection | collection_selection)*
  public static boolean spel(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "spel")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SPEL, "<spel>");
    r = spel_0(b, l + 1);
    r = r && spel_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // string_literal | number_literal | SHARP field_or_method_name
  private static boolean spel_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "spel_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = string_literal(b, l + 1);
    if (!r) r = number_literal(b, l + 1);
    if (!r) r = spel_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // SHARP field_or_method_name
  private static boolean spel_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "spel_0_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, SHARP);
    r = r && field_or_method_name(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (field_or_method | method_call | map_selection | collection_projection | collection_selection)*
  private static boolean spel_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "spel_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!spel_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "spel_1", c)) break;
    }
    return true;
  }

  // field_or_method | method_call | map_selection | collection_projection | collection_selection
  private static boolean spel_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "spel_1_0")) return false;
    boolean r;
    r = field_or_method(b, l + 1);
    if (!r) r = method_call(b, l + 1);
    if (!r) r = map_selection(b, l + 1);
    if (!r) r = collection_projection(b, l + 1);
    if (!r) r = collection_selection(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // item_*
  static boolean spelFile(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "spelFile")) return false;
    while (true) {
      int c = current_position_(b);
      if (!item_(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "spelFile", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // SINGLE_QUOTED_STRING
  public static boolean string_literal(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "string_literal")) return false;
    if (!nextTokenIs(b, SINGLE_QUOTED_STRING)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, SINGLE_QUOTED_STRING);
    exit_section_(b, m, STRING_LITERAL, r);
    return r;
  }

}
