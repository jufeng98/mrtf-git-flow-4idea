package com.github.xiaolyuh.http.parser

import com.github.xiaolyuh.http.HttpFileType
import com.github.xiaolyuh.http.HttpLanguage
import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider

class HttpFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, HttpLanguage.INSTANCE) {
    override fun getFileType(): FileType {
        return HttpFileType.INSTANCE
    }

    override fun toString(): String {
        return "HTTP File"
    }
}
