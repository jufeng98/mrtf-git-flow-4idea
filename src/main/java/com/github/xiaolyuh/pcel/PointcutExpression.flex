// Copyright 2000-2022 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.github.xiaolyuh.pcel.laxer;

import com.intellij.lexer.FlexLexer;
import com.github.xiaolyuh.pcel.psi.PointcutExpressionTypes;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;

%%

%class PointcutExpressionLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

EXPR=[[:jletterdigit:]~!()*\-."/"@\^<>=]+

%state WAITING_VALUE

%%
"@annotation"                                             { return PointcutExpressionTypes.ANNOTATION; }
"@target"                                                 { return PointcutExpressionTypes.TARGET; }
"("                                                       { return PointcutExpressionTypes.LP; }
{EXPR}                                                    { return PointcutExpressionTypes.EXPR; }
")"                                                       { return PointcutExpressionTypes.RP; }
[^]                                                       { return TokenType.BAD_CHARACTER; }