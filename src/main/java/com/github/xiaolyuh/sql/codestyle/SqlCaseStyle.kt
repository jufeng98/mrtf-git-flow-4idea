package com.github.xiaolyuh.sql.codestyle

import com.dbn.common.util.Naming
import com.intellij.openapi.editor.Document
import com.intellij.psi.PsiElement
import com.intellij.util.ui.PresentableEnum

enum class SqlCaseStyle(val myId: Int, private val myDescription: String) : PresentableEnum {
    LOWER(1, "改为小写") {
        override fun doModifyKeyword(
            psiElement: PsiElement,
            document: Document,
        ) {
            val textRange = psiElement.textRange
            document.replaceString(textRange.startOffset, textRange.endOffset, psiElement.text.lowercase())
        }
    },
    UPPER(2, "改为大写") {
        override fun doModifyKeyword(
            psiElement: PsiElement,
            document: Document,
        ) {
            val textRange = psiElement.textRange
            document.replaceString(textRange.startOffset, textRange.endOffset, psiElement.text.uppercase())
        }
    },
    CAPITALIZE(3, "首字母大写") {
        override fun doModifyKeyword(
            psiElement: PsiElement,
            document: Document,
        ) {
            val textRange = psiElement.textRange
            document.replaceString(textRange.startOffset, textRange.endOffset, Naming.capitalize(psiElement.text))
        }
    },

    NOT_CHANGE(4, "不改变")
    ;

    open fun doModifyKeyword(
        psiElement: PsiElement,
        document: Document,
    ) {
    }

    override fun getPresentableText(): String {
        return myDescription
    }

    companion object {
        fun getByCode(code: Int): SqlCaseStyle {
            for (value in entries) {
                if (value.myId == code) {
                    return value
                }
            }
            throw RuntimeException("" + code)
        }
    }
}
