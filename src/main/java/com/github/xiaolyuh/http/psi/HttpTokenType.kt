package com.github.xiaolyuh.http.psi

import com.github.xiaolyuh.http.HttpLanguage
import com.intellij.psi.tree.IElementType
import org.jetbrains.annotations.NonNls

class HttpTokenType(debugName: @NonNls String) : IElementType(debugName, HttpLanguage.INSTANCE) {
    override fun toString(): String {
        return HttpTokenType::class.java.simpleName + "." + super.toString()
    }
}
