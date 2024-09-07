package com.github.xiaolyuh.sql.gutter

import com.dbn.connection.mapping.FileConnectionContextManager
import com.dbn.language.common.DBLanguagePsiFile
import com.dbn.language.common.element.impl.NamedElementType
import com.dbn.language.common.psi.ExecutablePsiElement
import com.dbn.language.sql.SQLLanguage
import com.github.xiaolyuh.sql.psi.SqlRoot
import com.intellij.lang.ASTNode
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.testFramework.runInEdtAndWait

class MockExecutablePsiElement(
    astNode: ASTNode?,
    elementType: NamedElementType?,
    private val sqlRoot: SqlRoot,
) :
    ExecutablePsiElement(
        astNode,
        elementType
    ) {

    override fun getText(): String {
        var sql = ""
        runInEdtAndWait {
            sql = sqlRoot.text
        }
        return sql
    }

    override fun prepareStatementText(): String {
        var sql = ""
        runInEdtAndWait {
            sql = sqlRoot.text
        }
        return sql
    }


    override fun getFile(): DBLanguagePsiFile {
        var project: Project? = null
        var virtualFile: VirtualFile? = null
        runInEdtAndWait {
            project = sqlRoot.project
            virtualFile = sqlRoot.containingFile.virtualFile
        }
        val contextManager = FileConnectionContextManager.getInstance(project!!)
        return DBLanguagePsiFile.createFromText(
            project!!,
            "test",
            SQLLanguage.INSTANCE.mainLanguageDialect,
            "select 1",
            contextManager.getConnection(virtualFile!!),
            contextManager.getDatabaseSchema(virtualFile!!)
        )!!
    }

    override fun isQuery(): Boolean {
        return true
    }

}
