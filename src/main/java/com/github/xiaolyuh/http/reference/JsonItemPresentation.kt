package com.github.xiaolyuh.http.reference

import com.intellij.navigation.ItemPresentation
import javax.swing.Icon

object JsonItemPresentation : ItemPresentation {

    override fun getPresentableText(): String {
        return "跳转到对应的Bean字段"
    }

    override fun getIcon(unused: Boolean): Icon? {
        return null
    }

}
