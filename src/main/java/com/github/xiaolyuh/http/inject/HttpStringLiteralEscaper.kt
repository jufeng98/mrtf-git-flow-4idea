package com.github.xiaolyuh.http.inject

import com.intellij.json.psi.impl.JSStringLiteralEscaper
import com.intellij.openapi.util.Ref
import com.intellij.openapi.util.TextRange
import com.intellij.psi.LiteralTextEscaper
import com.intellij.psi.PsiLanguageInjectionHost
import kotlin.math.min

class HttpStringLiteralEscaper<T : PsiLanguageInjectionHost>(host: T) : LiteralTextEscaper<T>(host) {
    private lateinit var outSourceOffsets: IntArray
    override fun decode(rangeInsideHost: TextRange, outChars: StringBuilder): Boolean {
        val subText = rangeInsideHost.substring(myHost.text)

        val sourceOffsetsRef = Ref<IntArray>()
        val result = JSStringLiteralEscaper.parseStringCharacters(
            subText,
            outChars,
            sourceOffsetsRef,
            false,
            !isOneLine
        )
        outSourceOffsets = sourceOffsetsRef.get()
        return result
    }

    override fun getOffsetInHost(offsetInDecoded: Int, rangeInsideHost: TextRange): Int {
        val result = if (offsetInDecoded < outSourceOffsets.size) outSourceOffsets[offsetInDecoded] else -1
        if (result == -1) return -1
        return (min(result.toDouble(), rangeInsideHost.length.toDouble()) + rangeInsideHost.startOffset).toInt()
    }

    override fun isOneLine(): Boolean {
        return false
    }
}
