package com.github.xiaolyuh.i18n

import com.intellij.DynamicBundle
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.PropertyKey
import java.util.*

class UiBundle(pathToBundle: String) : DynamicBundle(pathToBundle) {
    override fun getMessage(key: @NonNls String, vararg params: Any?): @Nls String {
        return message(key, *params)
    }

    companion object {
        const val BUNDLE: String = "messages.ui"

        @JvmStatic
        fun message(
            key: @PropertyKey(resourceBundle = BUNDLE) String,
            vararg params: Any?,
        ): @Nls String {
            return messageOrDefault(ResourceBundle.getBundle(BUNDLE), key, null, *params) ?: ""
        }
    }
}
