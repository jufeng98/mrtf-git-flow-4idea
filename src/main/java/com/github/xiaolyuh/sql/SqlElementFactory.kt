package com.github.xiaolyuh.sql

import com.github.xiaolyuh.sql.parser.SqlFile
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory

object SqlElementFactory {
    fun createSqlElement(project: Project, sql: String): PsiElement {
        val file = createFile(project, sql)
        return file.firstChild
    }

    private fun createFile(project: Project, text: String): SqlFile {
        val name = "dummy.mysql"
        return PsiFileFactory.getInstance(project).createFileFromText(name, SqlLanguage.INSTANCE, text) as SqlFile
    }
}
