package com.github.xiaolyuh.spring;

import com.github.xiaolyuh.sql.reference.SqlReference;
import com.github.xiaolyuh.sql.reference.TableReference;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        private static final Logger log = LoggerFactory.getLogger(SqlPsiReferenceProvider.class);

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

            List<SqlReference> columnTableAliasReferences = createColumnTableAliasReferences(sqlStatement, aliasMap,
                    sqlTableNames, startOffsetInParent);

            List<SqlTableName> sqlTableNameList = Collections.emptyList();
            List<TableReference> tableReferences = Collections.emptyList();

            if (sqlStatement.getCompoundSelectStmt() != null) {
                sqlTableNameList = SqlUtils.getSqlTableNames(sqlJoinClauses);

                tableReferences = createTableNameReferences(sqlStatement, sqlTableNameList, startOffsetInParent);
            } else if (sqlStatement.getDeleteStmtLimited() != null || sqlStatement.getUpdateStmtLimited() != null) {
                sqlTableNameList = new ArrayList<>(PsiTreeUtil.findChildrenOfType(sqlStatement, SqlTableName.class));

                tableReferences = createTableNameReferences(sqlStatement, sqlTableNameList, startOffsetInParent);
            }

            if (sqlTableNameList.isEmpty()) {
                return PsiReference.EMPTY_ARRAY;
            }

            List<TableReference> columnReferences = createColumnNameReferences(sqlStatement, sqlTableNameList, sqlColumnNames,
                    aliasMap, startOffsetInParent);

            List<SqlReference> tableAliasReferences = createTableAliasReferences(sqlTableNames, sqlStatement,
                    aliasMap, startOffsetInParent);

            List<PsiReference> references = Lists.newArrayList();
            references.addAll(columnTableAliasReferences);
            references.addAll(tableReferences);
            references.addAll(columnReferences);
            references.addAll(tableAliasReferences);

            return references.toArray(PsiReference[]::new);
        }

        private List<SqlReference> createTableAliasReferences(Collection<SqlTableName> sqlTableNames,
                                                              SqlStatement sqlStatement,
                                                              Map<String, List<SqlTableAlias>> aliasMap,
                                                              int startOffsetInParent) {
            Map<String, List<SqlTableName>> tableNameMap = sqlTableNames.stream()
                    .filter(it -> PsiTreeUtil.getNextSiblingOfType(it, SqlColumnName.class) != null)
                    .collect(Collectors.groupingBy(PsiElement::getText));

            return aliasMap.values().stream()
                    .map(it -> {
                        SqlTableAlias sqlTableAlias = it.get(0);
                        List<SqlTableName> tableNames = tableNameMap.get(sqlTableAlias.getText());
                        if (tableNames == null) {
                            return null;
                        }
                        TextRange textRange = sqlTableAlias.getTextRange().shiftLeft(startOffsetInParent);
                        return new SqlReference(sqlStatement, textRange, tableNames.toArray(PsiElement[]::new));
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

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
            SqlTableName sqlTableName = SqlUtils.getTableNameOfAlias(aliasMap, columnTableAliasName);
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

        private List<SqlReference> createColumnTableAliasReferences(SqlStatement sqlStatement,
                                                                    Map<String, List<SqlTableAlias>> aliasMap,
                                                                    Collection<SqlTableName> sqlTableNames,
                                                                    int startOffsetInParent) {
            if (aliasMap.isEmpty()) {
                return Lists.newArrayList();
            }

            return sqlTableNames.stream()
                    .filter(it -> PsiTreeUtil.getNextSiblingOfType(it, SqlColumnName.class) != null)
                    .map(it -> {
                        TextRange textRange = it.getTextRange().shiftLeft(startOffsetInParent);

                        String name = it.getText();
                        List<SqlTableAlias> sqlTableAliases = aliasMap.get(name);
                        if (sqlTableAliases == null) {
                            return new SqlReference(sqlStatement, textRange);
                        }

                        if (sqlTableAliases.size() > 1) {
                            log.warn("根据{}找到多个表别名:{},直接取第一个", name, sqlTableAliases);
                        }

                        return new SqlReference(sqlStatement, textRange, sqlTableAliases.get(0));
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