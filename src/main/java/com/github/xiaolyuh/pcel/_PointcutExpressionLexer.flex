package com.github.xiaolyuh.pcel;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;
import static com.github.xiaolyuh.pcel.psi.PointcutExpressionTypes.*;

%%

%{
  public _PointcutExpressionLexer() {
    this((java.io.Reader)null);
  }
%}

%public
%class _PointcutExpressionLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

EOL=\R
WHITE_SPACE=\s+

EXPR=\([\w*!+.\s()]+\)
METHOD_REFERENCE=[:letter:][a-zA-Z_0-9]*\(\)
OR_OPERATOR=[ \t\n\x0B\f\r]*\|\|[ \t\n\x0B\f\r]*
AND_OPERATOR=[ \t\n\x0B\f\r]*&&[ \t\n\x0B\f\r]*

%%
<YYINITIAL> {
  {WHITE_SPACE}            { return WHITE_SPACE; }

  "@annotation"            { return ANNOTATION; }
  "@target"                { return ANNO_TARGET; }
  "execution"              { return EXECUTION; }
  "bean"                   { return BEAN; }

  {EXPR}                   { return EXPR; }
  {METHOD_REFERENCE}       { return METHOD_REFERENCE; }
  {OR_OPERATOR}            { return OR_OPERATOR; }
  {AND_OPERATOR}           { return AND_OPERATOR; }

}

[^] { return BAD_CHARACTER; }
