package com.github.xiaolyuh.sql.codestyle

import com.github.xiaolyuh.sql.SqlLanguage
import com.intellij.application.options.TabbedLanguageCodeStylePanel
import com.intellij.psi.codeStyle.CodeStyleSettings

class SqlCodeStyleMainPanel(currentSettings: CodeStyleSettings, settings: CodeStyleSettings) :
    TabbedLanguageCodeStylePanel(SqlLanguage.INSTANCE, currentSettings, settings) {
    override fun initTabs(settings: CodeStyleSettings?) {
        addTab(SqlCaseCodeStyleSpacesPanel(settings))
        addIndentOptionsTab(settings)
        addSpacesTab(settings)
        addBlankLinesTab(settings)
        addWrappingAndBracesTab(settings)
    }

}
