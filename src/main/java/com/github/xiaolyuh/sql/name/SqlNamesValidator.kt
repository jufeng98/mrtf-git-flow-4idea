package com.github.xiaolyuh.sql.name

import com.github.xiaolyuh.sql.parser.SqlAdapter
import com.github.xiaolyuh.sql.psi.SqlTypes
import com.github.xiaolyuh.utils.SqlUtils
import com.intellij.lang.refactoring.NamesValidator
import com.intellij.openapi.project.Project

class SqlNamesValidator : NamesValidator {
    private val myLexer = SqlAdapter()

    override fun isKeyword(name: String, project: Project): Boolean {
        myLexer.start(name)
        val tokenType = myLexer.tokenType
        return SqlUtils.isKeyword(tokenType) && myLexer.tokenEnd == name.length
    }

    override fun isIdentifier(name: String, project: Project): Boolean {
        myLexer.start(name)
        return myLexer.tokenType == SqlTypes.ID && myLexer.tokenEnd == name.length
    }
}
