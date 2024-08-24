package com.github.xiaolyuh.http;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;
import static com.github.xiaolyuh.http.psi.HttpTypes.*;

%%

%{
  public _HttpLexer() {
    this((java.io.Reader)null);
  }
%}

%public
%class _HttpLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

EOL=\R
WHITE_SPACE=\s+

PATH=([a-zA-Z0-9_\-./]+"/"?)+
URL_DESC=(https|http|\{\{)[-a-zA-Z0-9+&@${}()#/%?=~_|!:,.; ]*[-a-zA-Z0-9+,&@${}()#/%=~_| ]
HEADER_DESC=[a-zA-Z\-]+:[a-zA-Z0-9,${}()_=;\\\".\-*:/ ]+
REQUEST_COMMENT=#.*
LINE_COMMENT="//".*
URL_FORM_ENCODE=[a-zA-Z0-9,&$={}()]*
JSON_TEXT=[{\[][\u4E00-\u9FA5a-zA-Z0-9._,\":'&$【】~{}()\[\]\s\-/：（）、!！+=，]*
XML_TEXT=(<| <)[^#]*
MULTIPART_SEPERATE=--[^-]+
MULTIPART_SEPERATE_END=--[^-]+--
VARIABLE=\{\{[a-zA-Z0-9.(),$]+}}
JS_SCRIPT=\{%[^#]*%}
SPACE=[ \t\n\x0B\f\r]+

%%
<YYINITIAL> {
  {WHITE_SPACE}                  { return WHITE_SPACE; }

  "<"                            { return T_LT; }
  ">"                            { return T_RT; }
  "GET"                          { return GET; }
  "POST"                         { return POST; }
  "DELETE"                       { return DELETE; }
  "PUT"                          { return PUT; }

  {PATH}                         { return PATH; }
  {URL_DESC}                     { return URL_DESC; }
  {HEADER_DESC}                  { return HEADER_DESC; }
  {REQUEST_COMMENT}              { return REQUEST_COMMENT; }
  {LINE_COMMENT}                 { return LINE_COMMENT; }
  {URL_FORM_ENCODE}              { return URL_FORM_ENCODE; }
  {JSON_TEXT}                    { return JSON_TEXT; }
  {XML_TEXT}                     { return XML_TEXT; }
  {MULTIPART_SEPERATE}           { return MULTIPART_SEPERATE; }
  {MULTIPART_SEPERATE_END}       { return MULTIPART_SEPERATE_END; }
  {VARIABLE}                     { return VARIABLE; }
  {JS_SCRIPT}                    { return JS_SCRIPT; }
  {SPACE}                        { return SPACE; }

}

[^] { return BAD_CHARACTER; }
