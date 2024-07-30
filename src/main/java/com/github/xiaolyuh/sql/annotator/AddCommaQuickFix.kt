package com.github.xiaolyuh.sql.annotator

import com.github.xiaolyuh.sql.SqlElementFactory
import com.intellij.codeInsight.intention.impl.BaseIntentionAction
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile

class AddCommaQuickFix(val element: PsiElement) : BaseIntentionAction() {
    override fun getFamilyName(): String {
        return text
    }

    override fun getText(): String {
        @Suppress("DialogTitleCapitalization")
        return "添加逗号"
    }

    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean {
        return true
    }

    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
        ApplicationManager.getApplication().invokeLater {
            WriteCommandAction.writeCommandAction(element.project).run<Exception> {
                val commaElement = SqlElementFactory.createSqlElement(element.project, ",")
                element.parent.addAfter(commaElement, element)
            }
        }
    }
}
