package com.github.xiaolyuh.i18n

import com.intellij.BundleBase.messageOrDefault
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.PropertyKey
import java.util.*

object UiBundle {
    const val BUNDLE: String = "messages.ui"

    fun message(key: @PropertyKey(resourceBundle = BUNDLE) String, vararg params: Any): @Nls String {
        return messageOrDefault(ResourceBundle.getBundle(BUNDLE), key, "", *params)
    }

}
