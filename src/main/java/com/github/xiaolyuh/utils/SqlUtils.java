package com.github.xiaolyuh.utils;

import com.github.xiaolyuh.sql.psi.SqlColumnAlias;
import com.github.xiaolyuh.sql.psi.SqlColumnName;
import com.github.xiaolyuh.sql.psi.SqlCompoundSelectStmt;
import com.github.xiaolyuh.sql.psi.SqlGroupingTerm;
import com.github.xiaolyuh.sql.psi.SqlJoinClause;
import com.github.xiaolyuh.sql.psi.SqlOrderingTerm;
import com.github.xiaolyuh.sql.psi.SqlSelectStmt;
import com.github.xiaolyuh.sql.psi.SqlTableAlias;
import com.github.xiaolyuh.sql.psi.SqlTableName;
import com.github.xiaolyuh.sql.psi.SqlTableOrSubquery;
import com.github.xiaolyuh.sql.psi.SqlTypes;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtil;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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

    /**
     * 获取列名前的表别名
     */
    public static @Nullable SqlTableName getTableAliasNameOfColumn(SqlColumnName columnName) {
        return PsiTreeUtil.getPrevSiblingOfType(columnName, SqlTableName.class);
    }

    /**
     * 判断element是否属于列名前的表别名(只适用于select)
     */
    public static boolean isColumnTableAlias(SqlTableName element) {
        return PsiTreeUtil.getNextSiblingOfType(element, SqlColumnName.class) != null
                || getNextSiblingOfType(element, SqlTypes.MULTIPLY) != null;
    }

    /**
     * 若sqlColumnName位于order by或group by里,则尝试找到其对应的列别名
     */
    public static @Nullable SqlColumnAlias getColumnAliasIfInOrderGroupBy(SqlColumnName sqlColumnName) {
        SqlOrderingTerm sqlOrderingTerm = PsiTreeUtil.getParentOfType(sqlColumnName, SqlOrderingTerm.class);
        SqlGroupingTerm sqlGroupingTerm = PsiTreeUtil.getParentOfType(sqlColumnName, SqlGroupingTerm.class);
        if (sqlGroupingTerm == null && sqlOrderingTerm == null) {
            return null;
        }

        SqlCompoundSelectStmt sqlCompoundSelectStmt = PsiTreeUtil.getParentOfType(sqlColumnName, SqlCompoundSelectStmt.class);

        Collection<SqlColumnAlias> columnAliases = PsiTreeUtil.findChildrenOfType(sqlCompoundSelectStmt, SqlColumnAlias.class);

        String text = sqlColumnName.getText();
        Optional<SqlColumnAlias> optionalSqlColumnAlias = columnAliases.stream()
                .filter(it -> text.equals(it.getText()))
                .findFirst();

        return optionalSqlColumnAlias.orElse(null);
    }


    public static PsiElement getNextSiblingOfType(@Nullable PsiElement sibling, IElementType elementType) {
        if (sibling == null) {
            return null;
        }

        for (PsiElement nextSibling = sibling.getNextSibling(); nextSibling != null; nextSibling = nextSibling.getNextSibling()) {
            if (PsiUtil.getElementType(nextSibling) == elementType) {
                return nextSibling;
            }
        }

        return null;
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

    public static PsiElement getLastChildElement(PsiElement psiElement) {
        PsiElement lastChild = psiElement.getLastChild();
        if (lastChild != null) {
            return getLastChildElement(lastChild);
        }

        return psiElement;
    }
}
