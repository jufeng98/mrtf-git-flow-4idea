package com.github.xiaolyuh.utils;

import com.dbn.language.sql.SqlElementFactory;
import com.github.xiaolyuh.sql.psi.SqlColumnName;
import com.github.xiaolyuh.sql.psi.SqlJoinClause;
import com.github.xiaolyuh.sql.psi.SqlSelectStmt;
import com.github.xiaolyuh.sql.psi.SqlTableAlias;
import com.github.xiaolyuh.sql.psi.SqlTableName;
import com.github.xiaolyuh.sql.psi.SqlTableOrSubquery;
import com.github.xiaolyuh.sql.psi.SqlTypes;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilCore;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class SqlUtils {
    public static Set<IElementType> SQL_KEYWORDS = Set.of(
            SqlTypes.SELECT,
            SqlTypes.DELETE,
            SqlTypes.ADD,
            SqlTypes.UPDATE,
            SqlTypes.FROM,
            SqlTypes.INNER,
            SqlTypes.LEFT,
            SqlTypes.JOIN,
            SqlTypes.WHEN,
            SqlTypes.WHERE,
            SqlTypes.CASE,
            SqlTypes.IF,
            SqlTypes.AS,
            SqlTypes.ON,
            SqlTypes.AND,
            SqlTypes.IS,
            SqlTypes.NOT,
            SqlTypes.NULL,
            SqlTypes.CREATE,
            SqlTypes.EXISTS,
            SqlTypes.NO,
            SqlTypes.END,
            SqlTypes.FOR,
            SqlTypes.OR,
            SqlTypes.COLUMN,
            SqlTypes.COLUMN_COMMENT,
            SqlTypes.DEFAULT,
            SqlTypes.LIKE,
            SqlTypes.ELSE,
            SqlTypes.IN,
            SqlTypes.TO,
            SqlTypes.CAST,
            SqlTypes.LIMIT,
            SqlTypes.OFFSET,
            SqlTypes.OF,
            SqlTypes.TABLE,
            SqlTypes.INDEX,
            SqlTypes.ASC,
            SqlTypes.DESC,
            SqlTypes.BETWEEN,
            SqlTypes.BY,
            SqlTypes.ORDER,
            SqlTypes.VALUES,
            SqlTypes.UNIQUE,
            SqlTypes.UNION,
            SqlTypes.TRUE,
            SqlTypes.FALSE,
            SqlTypes.SET,
            SqlTypes.PRIMARY,
            SqlTypes.KEY,
            SqlTypes.HAVING,
            SqlTypes.GROUP
    );

    public static boolean isKeyword(IElementType tokenType) {
        return SqlUtils.SQL_KEYWORDS.contains(tokenType);
    }

    public static Map<IElementType, List<PsiElement>> initSampleLowerMap(Project project, String sql) {
        PsiElement sqlRoot = SqlElementFactory.createSqlElement(project, sql);
        Collection<PsiElement> children = PsiTreeUtil.findChildrenOfType(sqlRoot, PsiElement.class);
        return children.stream()
                .collect(Collectors.groupingBy(PsiUtilCore::getElementType));
    }

    public static Map<IElementType, List<PsiElement>> initSampleUpperMap(Project project, String sql) {
        PsiElement sqlRoot = SqlElementFactory.createSqlElement(project, sql.toUpperCase());
        Collection<PsiElement> children = PsiTreeUtil.findChildrenOfType(sqlRoot, PsiElement.class);
        return children.stream()
                .collect(Collectors.groupingBy(PsiUtilCore::getElementType));
    }

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
