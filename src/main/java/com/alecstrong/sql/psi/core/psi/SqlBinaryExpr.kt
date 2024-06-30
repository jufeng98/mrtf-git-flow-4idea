package com.alecstrong.sql.psi.core.psi

import com.github.xiaolyuh.sql.psi.SqlExpr

interface SqlBinaryExpr : SqlExpr {
    fun getExprList(): List<SqlExpr>
}