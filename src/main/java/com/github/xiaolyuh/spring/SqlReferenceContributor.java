package com.github.xiaolyuh.spring;

import com.github.xiaolyuh.reference.CommonReference;
import com.github.xiaolyuh.reference.TableReference;
import com.github.xiaolyuh.sql.psi.SqlColumnName;
import com.github.xiaolyuh.sql.psi.SqlJoinClause;
import com.github.xiaolyuh.sql.psi.SqlRoot;
import com.github.xiaolyuh.sql.psi.SqlStatement;
import com.github.xiaolyuh.sql.psi.SqlTableAlias;
import com.github.xiaolyuh.sql.psi.SqlTableName;
import com.github.xiaolyuh.sql.psi.SqlTableOrSubquery;
import com.intellij.openapi.util.Pair;
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

            Pair<List<CommonReference>, Map<String, List<SqlTableAlias>>> pair = createTableAliasReferences(sqlStatement,
                    sqlJoinClauses, sqlTableNames, startOffsetInParent);

            List<SqlTableName> sqlTableNameList = Collections.emptyList();
            List<TableReference> tableReferences = Collections.emptyList();

            if (sqlStatement.getCompoundSelectStmt() != null) {
                sqlTableNameList = getSqlTableNames(sqlJoinClauses);

                tableReferences = createTableNameReferences(sqlStatement, sqlTableNameList, startOffsetInParent);
            } else if (sqlStatement.getDeleteStmtLimited() != null || sqlStatement.getUpdateStmtLimited() != null) {
                sqlTableNameList = new ArrayList<>(PsiTreeUtil.findChildrenOfType(sqlStatement, SqlTableName.class));

                tableReferences = createTableNameReferences(sqlStatement, sqlTableNameList, startOffsetInParent);
            }

            if (sqlTableNameList.isEmpty()) {
                return PsiReference.EMPTY_ARRAY;
            }

            List<TableReference> columnReferences = createColumnNameReferences(sqlStatement, sqlTableNameList, sqlColumnNames,
                    pair.getSecond(), startOffsetInParent);

            List<PsiReference> references = Lists.newArrayList();

            List<CommonReference> tableAliasReferences = pair.getFirst();
            references.addAll(tableAliasReferences);

            references.addAll(tableReferences);
            references.addAll(columnReferences);

            return references.toArray(PsiReference[]::new);
        }

        private List<TableReference> createColumnNameReferences(SqlStatement sqlStatement,
                                                                List<SqlTableName> sqlTableNames,
                                                                Collection<SqlColumnName> sqlColumnNames,
                                                                Map<String, List<SqlTableAlias>> aliasMap,
                                                                int startOffsetInParent) {
            return sqlColumnNames.stream()
                    .map(sqlColumnName -> {
                        String columnTableAliasName = getTableAliasNameOfColumn(sqlColumnName);
                        TextRange textRange = sqlColumnName.getTextRange().shiftLeft(startOffsetInParent);

                        if (columnTableAliasName != null) {
                            return createColumnTableReference(sqlColumnName, columnTableAliasName, sqlStatement,
                                    aliasMap, textRange);
                        } else {
                            return new TableReference(sqlStatement, sqlTableNames, sqlColumnName, textRange);
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        @Nullable
        private TableReference createColumnTableReference(SqlColumnName sqlColumnName, String columnTableAliasName,
                                                          SqlStatement sqlStatement, Map<String, List<SqlTableAlias>> aliasMap,
                                                          TextRange textRange) {
            List<SqlTableAlias> sqlTableAliases = aliasMap.get(columnTableAliasName);
            if (sqlTableAliases == null) {
                return null;
            }

            SqlTableAlias sqlTableAlias = sqlTableAliases.get(0);
            SqlTableName sqlTableName = PsiTreeUtil.getPrevSiblingOfType(sqlTableAlias, SqlTableName.class);
            if (sqlTableName == null) {
                return null;
            }

            return new TableReference(sqlStatement, List.of(sqlTableName), sqlColumnName, textRange);
        }

        @Nullable
        private String getTableAliasNameOfColumn(SqlColumnName sqlColumnName) {
            SqlTableName sqlTableName = PsiTreeUtil.getPrevSiblingOfType(sqlColumnName, SqlTableName.class);
            if (sqlTableName == null) {
                return null;
            }

            return sqlTableName.getText();
        }

        private List<SqlTableName> getSqlTableNames(Collection<SqlJoinClause> sqlJoinClauses) {
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

        private List<TableReference> createTableNameReferences(SqlStatement sqlStatement,
                                                               List<SqlTableName> sqlTableNameList,
                                                               int startOffsetInParent) {
            return sqlTableNameList.stream()
                    .map(sqlTableName -> {
                        TextRange textRange = sqlTableName.getTextRange().shiftLeft(startOffsetInParent);
                        return new TableReference(sqlStatement, List.of(sqlTableName), null, textRange);
                    })
                    .collect(Collectors.toList());
        }

        private Pair<List<CommonReference>, Map<String, List<SqlTableAlias>>> createTableAliasReferences(SqlStatement sqlStatement,
                                                                                                         Collection<SqlJoinClause> sqlJoinClauses,
                                                                                                         Collection<SqlTableName> sqlTableNames,
                                                                                                         int startOffsetInParent) {
            Map<String, List<SqlTableAlias>> aliasMap = sqlJoinClauses.stream()
                    .map(sqlJoinClause -> {
                        List<SqlTableOrSubquery> tableOrSubqueryList = sqlJoinClause.getTableOrSubqueryList();
                        return tableOrSubqueryList.stream()
                                .map(SqlTableOrSubquery::getTableAlias)
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList());
                    })
                    .flatMap(Collection::stream)
                    .collect(Collectors.groupingBy(SqlTableAlias::getText));

            if (aliasMap.isEmpty()) {
                return Pair.create(Lists.newArrayList(), aliasMap);
            }

            List<CommonReference> references = sqlTableNames.stream()
                    .map(it -> {
                        String name = it.getText();
                        List<SqlTableAlias> sqlTableAliases = aliasMap.get(name);
                        if (sqlTableAliases == null) {
                            return null;
                        }

                        TextRange textRange = it.getTextRange().shiftLeft(startOffsetInParent);
                        return new CommonReference(sqlStatement, textRange, sqlTableAliases.get(0));
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            return Pair.create(references, aliasMap);
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