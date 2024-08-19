package com.github.xiaolyuh.http.psi

import com.github.xiaolyuh.http.HttpLanguage
import com.intellij.psi.tree.IElementType
import org.jetbrains.annotations.NonNls

class HttpElementType(debugName: @NonNls String) : IElementType(debugName, HttpLanguage.INSTANCE)
