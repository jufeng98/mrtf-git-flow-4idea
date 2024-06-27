package com.github.xiaolyuh.spel;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;
import static com.github.xiaolyuh.spel.psi.SpelTypes.*;

%%

%{
  public _SpelLexer() {
    this((java.io.Reader)null);
  }
%}

%public
%class _SpelLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

EOL=\R
WHITE_SPACE=\s+

SINGLE_QUOTED_STRING='([^\\'\r\n]|\\[^\r\n])*'
IDENTIFIER=[a-zA-Z][\w_\\$]*
PLUS=[ \t\n\x0B\f\r]*\+[ \t\n\x0B\f\r]*
NUMBER=-?(0|[1-9][0-9]*)(\.[0-9]+)?([eE][+-]?[0-9]*)?
COMMA=[ \t\n\x0B\f\r]*,[ \t\n\x0B\f\r]*
EXPR=[\w=']+
STATIC_REFERENCE=\([\w.]+\)

%%
<YYINITIAL> {
  {WHITE_SPACE}                { return WHITE_SPACE; }

  "#"                          { return SHARP; }
  "."                          { return DOT; }
  ".!["                        { return PROJECTION; }
  ".?["                        { return SELECTION; }
  "["                          { return L_BRACKET; }
  "]"                          { return R_BRACKET; }

  {SINGLE_QUOTED_STRING}       { return SINGLE_QUOTED_STRING; }
  {IDENTIFIER}                 { return IDENTIFIER; }
  {PLUS}                       { return PLUS; }
  {NUMBER}                     { return NUMBER; }
  {COMMA}                      { return COMMA; }
  {EXPR}                       { return EXPR; }
  {STATIC_REFERENCE}           { return STATIC_REFERENCE; }

}

[^] { return BAD_CHARACTER; }
