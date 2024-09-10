package com.github.xiaolyuh.http.reference

import com.intellij.navigation.ItemPresentation
import javax.swing.Icon

object HttpItemPresentation : ItemPresentation {

    override fun getPresentableText(): String {
        return "搜索对应的Controller接口"
    }

    override fun getIcon(unused: Boolean): Icon? {
        return null
    }

}
