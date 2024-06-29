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
  // L_BRACE root (COMMA root)* R_BRACE
  public static boolean array_wrap(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array_wrap")) return false;
    if (!nextTokenIs(b, L_BRACE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ARRAY_WRAP, null);
    r = consumeToken(b, L_BRACE);
    p = r; // pin = 1
    r = r && report_error_(b, root(b, l + 1));
    r = p && report_error_(b, array_wrap_2(b, l + 1)) && r;
    r = p && consumeToken(b, R_BRACE) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (COMMA root)*
  private static boolean array_wrap_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array_wrap_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!array_wrap_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "array_wrap_2", c)) break;
    }
    return true;
  }

  // COMMA root
  private static boolean array_wrap_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array_wrap_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && root(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // PROJECTION field_recursive_call R_BRACKET
  public static boolean collection_projection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "collection_projection")) return false;
    if (!nextTokenIs(b, PROJECTION)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, COLLECTION_PROJECTION, null);
    r = consumeToken(b, PROJECTION);
    p = r; // pin = 1
    r = r && report_error_(b, field_recursive_call(b, l + 1));
    r = p && consumeToken(b, R_BRACKET) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // SELECTION selection_expression R_BRACKET
  public static boolean collection_selection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "collection_selection")) return false;
    if (!nextTokenIs(b, SELECTION)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, COLLECTION_SELECTION, null);
    r = consumeToken(b, SELECTION);
    p = r; // pin = 1
    r = r && report_error_(b, selection_expression(b, l + 1));
    r = p && consumeToken(b, R_BRACKET) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
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
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FIELD_OR_METHOD, null);
    r = consumeToken(b, DOT);
    p = r; // pin = 1
    r = r && report_error_(b, field_or_method_name(b, l + 1));
    r = p && field_or_method_2(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
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
  // L_BRACKET field_name R_BRACKET
  public static boolean map_selection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "map_selection")) return false;
    if (!nextTokenIs(b, L_BRACKET)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, MAP_SELECTION, null);
    r = consumeToken(b, L_BRACKET);
    p = r; // pin = 1
    r = r && report_error_(b, field_name(b, l + 1));
    r = p && consumeToken(b, R_BRACKET) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // L_PARENTHESES method_params? R_PARENTHESES
  public static boolean method_call(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "method_call")) return false;
    if (!nextTokenIs(b, L_PARENTHESES)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, METHOD_CALL, null);
    r = consumeToken(b, L_PARENTHESES);
    p = r; // pin = 1
    r = r && report_error_(b, method_call_1(b, l + 1));
    r = p && consumeToken(b, R_PARENTHESES) && r;
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
  // root
  public static boolean method_param(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "method_param")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, METHOD_PARAM, "<method param>");
    r = root(b, l + 1);
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
  // (string_literal | number_literal | SHARP field_or_method_name | static_t | array_wrap) (field_or_method | method_call | map_selection | collection_projection | collection_selection)*
  public static boolean root(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "root")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ROOT, "<root>");
    r = root_0(b, l + 1);
    r = r && root_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // string_literal | number_literal | SHARP field_or_method_name | static_t | array_wrap
  private static boolean root_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "root_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = string_literal(b, l + 1);
    if (!r) r = number_literal(b, l + 1);
    if (!r) r = root_0_2(b, l + 1);
    if (!r) r = static_t(b, l + 1);
    if (!r) r = array_wrap(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // SHARP field_or_method_name
  private static boolean root_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "root_0_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, SHARP);
    r = r && field_or_method_name(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (field_or_method | method_call | map_selection | collection_projection | collection_selection)*
  private static boolean root_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "root_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!root_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "root_1", c)) break;
    }
    return true;
  }

  // field_or_method | method_call | map_selection | collection_projection | collection_selection
  private static boolean root_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "root_1_0")) return false;
    boolean r;
    r = field_or_method(b, l + 1);
    if (!r) r = method_call(b, l + 1);
    if (!r) r = map_selection(b, l + 1);
    if (!r) r = collection_projection(b, l + 1);
    if (!r) r = collection_selection(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // PLUS root
  public static boolean root_combination(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "root_combination")) return false;
    if (!nextTokenIs(b, PLUS)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ROOT_COMBINATION, null);
    r = consumeToken(b, PLUS);
    p = r; // pin = 1
    r = r && root(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
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
  // root root_combination*
  public static boolean spel(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "spel")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SPEL, "<spel>");
    r = root(b, l + 1);
    r = r && spel_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // root_combination*
  private static boolean spel_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "spel_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!root_combination(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "spel_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // spel*
  static boolean spelFile(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "spelFile")) return false;
    while (true) {
      int c = current_position_(b);
      if (!spel(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "spelFile", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // STATIC_REFERENCE
  public static boolean static_t(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "static_t")) return false;
    if (!nextTokenIs(b, STATIC_REFERENCE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, STATIC_REFERENCE);
    exit_section_(b, m, STATIC_T, r);
    return r;
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
