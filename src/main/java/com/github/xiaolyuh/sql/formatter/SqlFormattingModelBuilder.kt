package com.github.xiaolyuh.sql.formatter

import com.github.xiaolyuh.sql.codestyle.SqlCodeStyleSettings
import com.intellij.formatting.FormattingContext
import com.intellij.formatting.FormattingModel
import com.intellij.formatting.FormattingModelBuilder
import com.intellij.formatting.FormattingModelProvider


class SqlFormattingModelBuilder : FormattingModelBuilder {

    override fun createModel(formattingContext: FormattingContext): FormattingModel {
        val settings = formattingContext.codeStyleSettings
        val customSettings = settings.getCustomSettings(SqlCodeStyleSettings::class.java)
        val containingFile = formattingContext.containingFile

        val sqlBlock = SqlBlock(customSettings, formattingContext.psiElement, null)

        return FormattingModelProvider.createFormattingModelForPsiFile(containingFile, sqlBlock, settings)
    }

}
