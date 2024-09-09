// This is a generated file. Not intended for manual editing.
package com.github.xiaolyuh.http.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static com.github.xiaolyuh.http.psi.HttpTypes.*;
import static com.github.xiaolyuh.http.parser.HttpParserUtil.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class HttpParser implements PsiParser, LightPsiParser {

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
    return httpFile(b, l + 1);
  }

  /* ********************************************************** */
  // ordinary_content | multipart_content
  public static boolean body(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "body")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BODY, "<body>");
    r = ordinary_content(b, l + 1);
    if (!r) r = multipart_content(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // T_LT file_path
  public static boolean file(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "file")) return false;
    if (!nextTokenIs(b, T_LT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, T_LT);
    r = r && file_path(b, l + 1);
    exit_section_(b, m, FILE, r);
    return r;
  }

  /* ********************************************************** */
  // PATH
  public static boolean file_path(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "file_path")) return false;
    if (!nextTokenIs(b, PATH)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, PATH);
    exit_section_(b, m, FILE_PATH, r);
    return r;
  }

  /* ********************************************************** */
  // HEADER_DESC
  public static boolean header(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "header")) return false;
    if (!nextTokenIs(b, HEADER_DESC)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, HEADER_DESC);
    exit_section_(b, m, HEADER, r);
    return r;
  }

  /* ********************************************************** */
  // header*
  public static boolean headers(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "headers")) return false;
    Marker m = enter_section_(b, l, _NONE_, HEADERS, "<headers>");
    while (true) {
      int c = current_position_(b);
      if (!header(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "headers", c)) break;
    }
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // request*
  static boolean httpFile(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "httpFile")) return false;
    while (true) {
      int c = current_position_(b);
      if (!request(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "httpFile", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // GET | POST | DELETE | PUT
  public static boolean method(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "method")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, METHOD, "<method>");
    r = consumeToken(b, GET);
    if (!r) r = consumeToken(b, POST);
    if (!r) r = consumeToken(b, DELETE);
    if (!r) r = consumeToken(b, PUT);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // MULTIPART_SEPERATE header* ordinary_content
  public static boolean multipart_body(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "multipart_body")) return false;
    if (!nextTokenIs(b, MULTIPART_SEPERATE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, MULTIPART_SEPERATE);
    r = r && multipart_body_1(b, l + 1);
    r = r && ordinary_content(b, l + 1);
    exit_section_(b, m, MULTIPART_BODY, r);
    return r;
  }

  // header*
  private static boolean multipart_body_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "multipart_body_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!header(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "multipart_body_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // multipart_body* MULTIPART_SEPERATE_END
  public static boolean multipart_content(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "multipart_content")) return false;
    if (!nextTokenIs(b, "<multipart content>", MULTIPART_SEPERATE, MULTIPART_SEPERATE_END)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, MULTIPART_CONTENT, "<multipart content>");
    r = multipart_content_0(b, l + 1);
    r = r && consumeToken(b, MULTIPART_SEPERATE_END);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // multipart_body*
  private static boolean multipart_content_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "multipart_content_0")) return false;
    while (true) {
      int c = current_position_(b);
      if (!multipart_body(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "multipart_content_0", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // file | URL_FORM_ENCODE | JSON_TEXT | XML_TEXT
  public static boolean ordinary_content(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ordinary_content")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ORDINARY_CONTENT, "<ordinary content>");
    r = file(b, l + 1);
    if (!r) r = consumeToken(b, URL_FORM_ENCODE);
    if (!r) r = consumeToken(b, JSON_TEXT);
    if (!r) r = consumeToken(b, XML_TEXT);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // T_RT_DBL file_path
  public static boolean output_file(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "output_file")) return false;
    if (!nextTokenIs(b, T_RT_DBL)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, T_RT_DBL);
    r = r && file_path(b, l + 1);
    exit_section_(b, m, OUTPUT_FILE, r);
    return r;
  }

  /* ********************************************************** */
  // method url version? headers? body? script? output_file? | script
  public static boolean request(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "request")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, REQUEST, "<request>");
    r = request_0(b, l + 1);
    if (!r) r = script(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // method url version? headers? body? script? output_file?
  private static boolean request_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "request_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = method(b, l + 1);
    r = r && url(b, l + 1);
    r = r && request_0_2(b, l + 1);
    r = r && request_0_3(b, l + 1);
    r = r && request_0_4(b, l + 1);
    r = r && request_0_5(b, l + 1);
    r = r && request_0_6(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // version?
  private static boolean request_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "request_0_2")) return false;
    version(b, l + 1);
    return true;
  }

  // headers?
  private static boolean request_0_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "request_0_3")) return false;
    headers(b, l + 1);
    return true;
  }

  // body?
  private static boolean request_0_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "request_0_4")) return false;
    body(b, l + 1);
    return true;
  }

  // script?
  private static boolean request_0_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "request_0_5")) return false;
    script(b, l + 1);
    return true;
  }

  // output_file?
  private static boolean request_0_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "request_0_6")) return false;
    output_file(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // T_RT JS_SCRIPT
  public static boolean script(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "script")) return false;
    if (!nextTokenIs(b, T_RT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, T_RT, JS_SCRIPT);
    exit_section_(b, m, SCRIPT, r);
    return r;
  }

  /* ********************************************************** */
  // URL_DESC
  public static boolean url(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "url")) return false;
    if (!nextTokenIs(b, URL_DESC)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, URL_DESC);
    exit_section_(b, m, URL, r);
    return r;
  }

  /* ********************************************************** */
  // 'HTTP/1.1' | 'HTTP/2.0'
  public static boolean version(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "version")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, VERSION, "<version>");
    r = consumeToken(b, "HTTP/1.1");
    if (!r) r = consumeToken(b, "HTTP/2.0");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

}
