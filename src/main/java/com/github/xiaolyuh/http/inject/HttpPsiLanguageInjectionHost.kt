package com.github.xiaolyuh.http.inject

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.json.psi.impl.JSStringLiteralEscaper
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiLanguageInjectionHost
import com.intellij.psi.impl.source.tree.LeafElement

open class HttpPsiLanguageInjectionHost(node: ASTNode) : ASTWrapperPsiElement(node), PsiLanguageInjectionHost {
    override fun isValidHost(): Boolean {
        return true
    }

    override fun updateText(text: String): PsiLanguageInjectionHost {
        val valueNode = node.firstChildNode
        assert(valueNode is LeafElement)
        (valueNode as LeafElement).replaceWithText(text)
        return this
    }

    override fun createLiteralTextEscaper(): JSStringLiteralEscaper<PsiLanguageInjectionHost?> {
        return object : JSStringLiteralEscaper<PsiLanguageInjectionHost?>(this) {
            override fun isRegExpLiteral(): Boolean {
                return false
            }

            override fun isOneLine(): Boolean {
                return false
            }
        }
    }
}
