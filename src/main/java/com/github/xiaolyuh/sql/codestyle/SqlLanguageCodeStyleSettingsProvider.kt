package com.github.xiaolyuh.sql.codestyle

import com.github.xiaolyuh.sql.SqlLanguage
import com.intellij.lang.Language
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider

class SqlLanguageCodeStyleSettingsProvider : LanguageCodeStyleSettingsProvider() {
    override fun getLanguage(): Language {
        return SqlLanguage.INSTANCE
    }

    override fun getCodeSample(settingsType: SettingsType): String {
        return """
            select 
                id,
                name,
                age
            from
                mall_user
        """.trimIndent()
    }
}