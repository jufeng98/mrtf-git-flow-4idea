package com.github.xiaolyuh.sql.codestyle

import com.github.xiaolyuh.sql.SqlLanguage
import com.intellij.application.options.IndentOptionsEditor
import com.intellij.application.options.SmartIndentOptionsEditor
import com.intellij.lang.Language
import com.intellij.psi.codeStyle.CommonCodeStyleSettings
import com.intellij.psi.codeStyle.CommonCodeStyleSettings.IndentOptions
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

    override fun getIndentOptionsEditor(): IndentOptionsEditor {
        return SmartIndentOptionsEditor()
    }

    override fun customizeDefaults(
        commonSettings: CommonCodeStyleSettings,
        indentOptions: IndentOptions,
    ) {
        indentOptions.INDENT_SIZE = 4
        // strip all blank lines by default
        commonSettings.KEEP_BLANK_LINES_IN_CODE = 0
    }
}