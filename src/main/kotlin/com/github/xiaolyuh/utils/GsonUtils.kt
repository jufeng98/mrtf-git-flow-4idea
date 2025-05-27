package com.github.xiaolyuh.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder

/**
 * @author yudong
 */
object GsonUtils {

    val gson: Gson = GsonBuilder()
        .setPrettyPrinting()
        .serializeNulls()
        .disableHtmlEscaping()
        .create()

}
