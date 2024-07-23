package com.github.xiaolyuh.utils;

import com.github.xiaolyuh.sql.psi.SqlColumnName;
import com.github.xiaolyuh.sql.psi.SqlJoinClause;
import com.github.xiaolyuh.sql.psi.SqlSelectStmt;
import com.github.xiaolyuh.sql.psi.SqlTableAlias;
import com.github.xiaolyuh.sql.psi.SqlTableName;
import com.github.xiaolyuh.sql.psi.SqlTableOrSubquery;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class SqlUtils {

    /**
     * 获取列名前的表别名
     */
    public static @Nullable SqlTableName getTableAliasNameOfColumn(SqlColumnName columnName) {
        return PsiTreeUtil.getPrevSiblingOfType(columnName, SqlTableName.class);
    }

    /**
     * 判断element是否属于列名前的表别名
     */
    public static boolean isColumnTableAlias(SqlTableName element) {
        return PsiTreeUtil.getNextSiblingOfType(element, SqlColumnName.class) != null;
    }

    /**
     * 获取列名前的表别名对应的表别名元素
     */
    public static @Nullable SqlTableAlias getTableAliasOfColumn(Map<String, List<SqlTableAlias>> aliasMap, SqlTableName columnTableAliasName) {
        List<SqlTableAlias> sqlTableAliases = aliasMap.get(columnTableAliasName.getText());
        if (sqlTableAliases == null) {
            return null;
        }

        SqlTableAlias sqlTableAlias = sqlTableAliases.get(0);

        if (sqlTableAliases.size() > 1) {
            SqlSelectStmt sqlSelectStmt = PsiTreeUtil.getParentOfType(columnTableAliasName, SqlSelectStmt.class);
            // 存在同样别名的表,需要判断下哪个是真正的表
            for (SqlTableAlias sqlTableAliasTmp : sqlTableAliases) {
                SqlSelectStmt sqlSelectStmtTmp = PsiTreeUtil.getParentOfType(sqlTableAliasTmp, SqlSelectStmt.class);
                if (sqlSelectStmtTmp == sqlSelectStmt) {
                    sqlTableAlias = sqlTableAliasTmp;
                    break;
                }
            }
        }

        return sqlTableAlias;
    }

    /**
     * 获取列名前表别名对应的的表名元素
     */
    public static @Nullable SqlTableName getTableNameOfAlias(Map<String, List<SqlTableAlias>> aliasMap, SqlTableName columnTableAliasName) {
        SqlTableAlias sqlTableAlias = getTableAliasOfColumn(aliasMap, columnTableAliasName);
        if (sqlTableAlias == null) {
            return null;
        }

        return getTableNameOfAlias(sqlTableAlias);
    }

    public static @Nullable SqlTableName getTableNameOfAlias(SqlTableAlias sqlTableAlias) {
        return PsiTreeUtil.getPrevSiblingOfType(sqlTableAlias, SqlTableName.class);
    }

    public static List<SqlTableName> getSqlTableNames(Collection<SqlJoinClause> sqlJoinClauses) {
        return sqlJoinClauses.stream()
                .map(sqlJoinClause -> {
                    List<SqlTableOrSubquery> tableOrSubqueryList = sqlJoinClause.getTableOrSubqueryList();
                    return tableOrSubqueryList.stream()
                            .map(SqlTableOrSubquery::getTableName)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
                })
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public static Map<String, List<SqlTableAlias>> getAliasMap(Collection<SqlJoinClause> sqlJoinClauses) {
        return sqlJoinClauses.stream()
                .map(sqlJoinClause -> {
                    List<SqlTableOrSubquery> tableOrSubqueryList = sqlJoinClause.getTableOrSubqueryList();
                    return tableOrSubqueryList.stream()
                            .map(SqlTableOrSubquery::getTableAlias)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
                })
                .flatMap(Collection::stream)
                .collect(Collectors.groupingBy(SqlTableAlias::getText));
    }

}
