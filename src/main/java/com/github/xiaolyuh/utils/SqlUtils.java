package com.github.xiaolyuh.utils;

import com.github.xiaolyuh.sql.psi.SqlJoinClause;
import com.github.xiaolyuh.sql.psi.SqlTableAlias;
import com.github.xiaolyuh.sql.psi.SqlTableName;
import com.github.xiaolyuh.sql.psi.SqlTableOrSubquery;
import com.intellij.psi.util.PsiTreeUtil;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class SqlUtils {

    public static SqlTableName getTableNameOfAlias(Map<String, List<SqlTableAlias>> aliasMap, String columnTableAliasName) {
        List<SqlTableAlias> sqlTableAliases = aliasMap.get(columnTableAliasName);
        if (sqlTableAliases == null) {
            return null;
        }

        SqlTableAlias sqlTableAlias = sqlTableAliases.get(0);

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
