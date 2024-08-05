package com.github.xiaolyuh.sql.structure

import com.github.xiaolyuh.sql.parser.SqlFile
import com.github.xiaolyuh.sql.psi.SqlColumnName
import com.github.xiaolyuh.sql.psi.SqlTableName
import com.intellij.ide.structureView.StructureViewModel.ElementInfoProvider
import com.intellij.ide.structureView.StructureViewModelBase
import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.openapi.editor.Editor

class SqlStructureViewModel(sqlFile: SqlFile, editor: Editor?) :
    StructureViewModelBase(sqlFile, editor, SqlStructureViewTreeElement(sqlFile, null)), ElementInfoProvider {

    init {
        withSuitableClasses(SqlTableName::class.java, SqlColumnName::class.java)
    }

    override fun isAlwaysShowsPlus(element: StructureViewTreeElement?): Boolean {
        return false
    }

    override fun isAlwaysLeaf(element: StructureViewTreeElement?): Boolean {
        return false
    }
}
