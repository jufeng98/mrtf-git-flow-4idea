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

SINGLE_QUOTED_STRING=[ \t\n\x0B\f\r]*'([^\\'\r\n]|\\[^\r\n])*'[ \t\n\x0B\f\r]*
IDENTIFIER=[a-zA-Z][\w$]*
PLUS=[ \t\n\x0B\f\r]*\+[ \t\n\x0B\f\r]*
NUMBER=[ \t\n\x0B\f\r]*-?(0|[1-9][0-9]*)(\.[0-9]+)?([eE][+-]?[0-9]*)?[ \t\n\x0B\f\r]*
COMMA=[ \t\n\x0B\f\r]*,[ \t\n\x0B\f\r]*
EXPR=[\w=']+
STATIC_REFERENCE=T\([a-zA-Z][\w.$]*\)

%%
<YYINITIAL> {
  {WHITE_SPACE}                { return WHITE_SPACE; }

  "#"                          { return SHARP; }
  "."                          { return DOT; }
  ".!["                        { return PROJECTION; }
  ".?["                        { return SELECTION; }
  "["                          { return L_BRACKET; }
  "]"                          { return R_BRACKET; }
  "("                          { return L_PARENTHESES; }
  ")"                          { return R_PARENTHESES; }
  "{"                          { return L_BRACE; }
  "}"                          { return R_BRACE; }

  {SINGLE_QUOTED_STRING}       { return SINGLE_QUOTED_STRING; }
  {IDENTIFIER}                 { return IDENTIFIER; }
  {PLUS}                       { return PLUS; }
  {NUMBER}                     { return NUMBER; }
  {COMMA}                      { return COMMA; }
  {EXPR}                       { return EXPR; }
  {STATIC_REFERENCE}           { return STATIC_REFERENCE; }

}

[^] { return BAD_CHARACTER; }
