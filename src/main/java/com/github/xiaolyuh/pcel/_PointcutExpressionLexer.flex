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

EXPR=\([\w*!/&+.\s()]+\)

%%
<YYINITIAL> {
  {WHITE_SPACE}       { return WHITE_SPACE; }

  "@annotation"       { return ANNOTATION; }
  "@target"           { return ANNO_TARGET; }
  "execution"         { return EXECUTION; }

  {EXPR}              { return EXPR; }

}

[^] { return BAD_CHARACTER; }
