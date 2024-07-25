package com.github.xiaolyuh.sql.formatter

import com.github.xiaolyuh.sql.codestyle.SqlCaseStyle
import com.github.xiaolyuh.sql.codestyle.SqlCodeStyleSettings
import com.github.xiaolyuh.utils.SqlUtils
import com.intellij.formatting.FormattingContext
import com.intellij.formatting.FormattingModel
import com.intellij.formatting.FormattingModelBuilder
import com.intellij.formatting.FormattingModelProvider
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Document
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.util.PsiTreeUtil


class SqlFormattingModelBuilder : FormattingModelBuilder {

    override fun createModel(formattingContext: FormattingContext): FormattingModel {
        val settings = formattingContext.codeStyleSettings
        val customSettings = settings.getCustomSettings(SqlCodeStyleSettings::class.java)
        val containingFile = formattingContext.containingFile

        handleCase(customSettings, containingFile)

        val sqlBlock = SqlBlock(settings, customSettings, formattingContext.psiElement, null)

        return FormattingModelProvider.createFormattingModelForPsiFile(containingFile, sqlBlock, settings)
    }

    private fun handleCase(customSettings: SqlCodeStyleSettings, containingFile: PsiFile) {
        val documentManager = PsiDocumentManager.getInstance(containingFile.project)
        val document: Document = documentManager.getDocument(containingFile) ?: return
        if (!document.isWritable) return

        val sqlCaseStyle = SqlCaseStyle.getByCode(customSettings.keywordCase)
        if (sqlCaseStyle == SqlCaseStyle.NOT_CHANGE) {
            return
        }

        ApplicationManager.getApplication().invokeAndWait {
            WriteCommandAction.runWriteCommandAction(containingFile.project) {
                val list = PsiTreeUtil.findChildrenOfType(containingFile, LeafPsiElement::class.java)
                list.forEach {
                    if (SqlUtils.isKeyword(it.elementType)) {
                        sqlCaseStyle.doModifyKeyword(it, document)
                    }
                }

                documentManager.commitDocument(document)
            }
        }

    }

}
