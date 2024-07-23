package com.github.xiaolyuh.sql.reference;

import com.github.xiaolyuh.sql.psi.SqlColumnName;
import com.github.xiaolyuh.sql.psi.SqlJoinClause;
import com.github.xiaolyuh.sql.psi.SqlRoot;
import com.github.xiaolyuh.sql.psi.SqlStatement;
import com.github.xiaolyuh.sql.psi.SqlTableAlias;
import com.github.xiaolyuh.sql.psi.SqlTableName;
import com.github.xiaolyuh.utils.SqlUtils;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


public class SqlReferenceContributor extends PsiReferenceContributor {

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(SqlStatement.class), new SqlPsiReferenceProvider());
    }

    public static class SqlPsiReferenceProvider extends PsiReferenceProvider {

        @Override
        public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element,
                                                               @NotNull ProcessingContext context) {
            SqlStatement sqlStatement = (SqlStatement) element;

            return createSqlReferences(sqlStatement);
        }


        private PsiReference[] createSqlReferences(SqlStatement sqlStatement) {
            SqlRoot sqlRoot = PsiTreeUtil.getParentOfType(sqlStatement, SqlRoot.class);
            if (sqlRoot == null) {
                return PsiReference.EMPTY_ARRAY;
            }

            Collection<SqlJoinClause> sqlJoinClauses = PsiTreeUtil.findChildrenOfType(sqlStatement, SqlJoinClause.class);

            Collection<SqlTableName> sqlTableNames = PsiTreeUtil.findChildrenOfType(sqlStatement, SqlTableName.class);

            Collection<SqlColumnName> sqlColumnNames = PsiTreeUtil.findChildrenOfType(sqlStatement, SqlColumnName.class);

            int startOffsetInParent = calOffset(sqlRoot);

            Map<String, List<SqlTableAlias>> aliasMap = SqlUtils.getAliasMap(sqlJoinClauses);

            List<ColumnTableAliasPsiReference> columnTableAliasReferences = createColumnTableAliasReferences(sqlStatement,
                    aliasMap, sqlTableNames, startOffsetInParent);

            List<SqlTableName> sqlTableNameList = Collections.emptyList();
            List<TableOrColumnPsiReference> tableOrColumnPsiReferences = Collections.emptyList();

            if (sqlStatement.getCompoundSelectStmt() != null) {
                sqlTableNameList = SqlUtils.getSqlTableNames(sqlJoinClauses);

                tableOrColumnPsiReferences = createTableNameReferences(sqlStatement, sqlTableNameList, startOffsetInParent);
            } else if (sqlStatement.getDeleteStmtLimited() != null
                    || sqlStatement.getUpdateStmtLimited() != null
                    || sqlStatement.getInsertStmt() != null) {
                sqlTableNameList = new ArrayList<>(PsiTreeUtil.findChildrenOfType(sqlStatement, SqlTableName.class));

                tableOrColumnPsiReferences = createTableNameReferences(sqlStatement, sqlTableNameList, startOffsetInParent);
            }

            if (sqlTableNameList.isEmpty()) {
                return PsiReference.EMPTY_ARRAY;
            }

            List<TableOrColumnPsiReference> columnReferences = createColumnNameReferences(sqlStatement, sqlTableNameList, sqlColumnNames,
                    aliasMap, startOffsetInParent);

            List<PsiReference> references = Lists.newArrayList();
            references.addAll(columnTableAliasReferences);
            references.addAll(tableOrColumnPsiReferences);
            references.addAll(columnReferences);

            return references.toArray(PsiReference[]::new);
        }

        private List<TableOrColumnPsiReference> createColumnNameReferences(SqlStatement sqlStatement,
                                                                           List<SqlTableName> sqlTableNames,
                                                                           Collection<SqlColumnName> sqlColumnNames,
                                                                           Map<String, List<SqlTableAlias>> aliasMap,
                                                                           int startOffsetInParent) {
            return sqlColumnNames.stream()
                    .map(sqlColumnName -> {
                        TextRange textRange = sqlColumnName.getTextRange().shiftLeft(startOffsetInParent);

                        if (sqlStatement.getInsertStmt() != null) {
                            return new TableOrColumnPsiReference(sqlStatement, sqlTableNames, sqlColumnName, textRange);
                        }

                        SqlTableName columnTableAliasName = SqlUtils.getTableAliasNameOfColumn(sqlColumnName);

                        if (columnTableAliasName != null) {
                            return createColumnTableReference(sqlColumnName, columnTableAliasName, sqlStatement,
                                    aliasMap, textRange);
                        } else {
                            return new TableOrColumnPsiReference(sqlStatement, sqlTableNames, sqlColumnName, textRange);
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        @Nullable
        private TableOrColumnPsiReference createColumnTableReference(SqlColumnName sqlColumnName,
                                                                     SqlTableName columnTableAliasName,
                                                                     SqlStatement sqlStatement,
                                                                     Map<String, List<SqlTableAlias>> aliasMap,
                                                                     TextRange textRange) {
            SqlTableName sqlTableName = SqlUtils.getTableNameOfAlias(aliasMap, columnTableAliasName);
            if (sqlTableName == null) {
                return null;
            }

            return new TableOrColumnPsiReference(sqlStatement, List.of(sqlTableName), sqlColumnName, textRange);
        }

        private List<TableOrColumnPsiReference> createTableNameReferences(SqlStatement sqlStatement,
                                                                          List<SqlTableName> sqlTableNameList,
                                                                          int startOffsetInParent) {
            return sqlTableNameList.stream()
                    .map(sqlTableName -> {
                        TextRange textRange = sqlTableName.getTextRange().shiftLeft(startOffsetInParent);
                        return new TableOrColumnPsiReference(sqlStatement, List.of(sqlTableName), null, textRange);
                    })
                    .collect(Collectors.toList());
        }

        private List<ColumnTableAliasPsiReference> createColumnTableAliasReferences(SqlStatement sqlStatement,
                                                                                    Map<String, List<SqlTableAlias>> aliasMap,
                                                                                    Collection<SqlTableName> sqlTableNames,
                                                                                    int startOffsetInParent) {
            if (aliasMap.isEmpty()) {
                return Lists.newArrayList();
            }

            return sqlTableNames.stream()
                    .filter(it -> PsiTreeUtil.getNextSiblingOfType(it, SqlColumnName.class) != null)
                    .map(columnTableAlias -> {
                        TextRange textRange = columnTableAlias.getTextRange().shiftLeft(startOffsetInParent);

                        SqlTableName sqlTableName = SqlUtils.getTableNameOfAlias(aliasMap, columnTableAlias);
                        if (sqlTableName == null) {
                            return new ColumnTableAliasPsiReference(sqlStatement, textRange, null);
                        }

                        SqlTableAlias sqlTableAlias = SqlUtils.getTableAliasOfColumn(aliasMap, columnTableAlias);
                        if (sqlTableAlias == null) {
                            return new ColumnTableAliasPsiReference(sqlStatement, textRange, null);
                        }

                        return new ColumnTableAliasPsiReference(sqlStatement, textRange, sqlTableAlias);
                    })
                    .collect(Collectors.toList());
        }

        private int calOffset(PsiElement psiElement) {
            PsiElement prevSibling = psiElement.getPrevSibling();
            if (prevSibling != null) {
                return prevSibling.getTextLength() + calOffset(prevSibling);
            } else {
                return 0;
            }
        }
    }

}