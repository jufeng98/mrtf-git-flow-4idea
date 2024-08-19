package com.github.xiaolyuh.http

import com.github.xiaolyuh.http.psi.HttpTypes
import com.intellij.lang.ASTFactory
import com.intellij.lang.DefaultASTFactory
import com.intellij.openapi.application.ApplicationManager
import com.intellij.psi.impl.source.tree.LeafElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.tree.IElementType

class HttpASTFactory : ASTFactory() {
    private val myDefaultASTFactory: DefaultASTFactory = ApplicationManager.getApplication().getService(
        DefaultASTFactory::class.java
    )

    override fun createLeaf(type: IElementType, text: CharSequence): LeafElement {
        if (type == HttpTypes.LINE_COMMENT || type == HttpTypes.REQUEST_COMMENT) {
            return myDefaultASTFactory.createComment(type, text)
        }

        return LeafPsiElement(type, text)
    }
}
