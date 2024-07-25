package com.github.xiaolyuh.sql.codestyle

import com.github.xiaolyuh.sql.SqlLanguage
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.codeStyle.CustomCodeStyleSettings

class SqlCodeStyleSettings(settings: CodeStyleSettings) : CustomCodeStyleSettings(SqlLanguage.INSTANCE.id, settings) {
    @JvmField
    var spaceBetweenSymbol: Boolean = true

    @JvmField
    var keywordCase = SqlCaseStyle.LOWER.myId

    @JvmField
    var removeAntiQuote: Boolean = true
}
