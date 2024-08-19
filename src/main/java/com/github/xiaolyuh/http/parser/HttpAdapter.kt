package com.github.xiaolyuh.http.parser

import com.github.xiaolyuh.http._HttpLexer
import com.intellij.lexer.FlexAdapter

class HttpAdapter : FlexAdapter(_HttpLexer(null))
