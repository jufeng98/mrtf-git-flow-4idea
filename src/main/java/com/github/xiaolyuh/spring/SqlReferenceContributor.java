package com.github.xiaolyuh.spring;

import com.github.xiaolyuh.reference.CommonReference;
import com.github.xiaolyuh.reference.TableReference;
import com.github.xiaolyuh.sql.psi.SqlJoinClause;
import com.github.xiaolyuh.sql.psi.SqlRoot;
import com.github.xiaolyuh.sql.psi.SqlStatement;
import com.github.xiaolyuh.sql.psi.SqlTableAlias;
import com.github.xiaolyuh.sql.psi.SqlTableName;
import com.github.xiaolyuh.sql.psi.SqlTableOrSubquery;
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

            int startOffsetInParent = calOffset(sqlRoot);

            Collection<SqlTableName> sqlTableNames = PsiTreeUtil.findChildrenOfType(sqlStatement, SqlTableName.class);

            List<PsiReference> references = Lists.newArrayList();

            List<CommonReference> tableAliasReferences = createTableAliasReferences(sqlStatement, sqlJoinClauses, sqlTableNames, startOffsetInParent);

            List<TableReference> tableReferences = createTableNameReferences(sqlStatement, sqlJoinClauses, startOffsetInParent);

            references.addAll(tableAliasReferences);
            references.addAll(tableReferences);

            return references.toArray(PsiReference[]::new);
        }

        private List<TableReference> createTableNameReferences(SqlStatement sqlStatement,
                                                               Collection<SqlJoinClause> sqlJoinClauses,
                                                               int startOffsetInParent) {
            return sqlJoinClauses.stream()
                    .map(sqlJoinClause -> {
                        List<SqlTableOrSubquery> tableOrSubqueryList = sqlJoinClause.getTableOrSubqueryList();
                        return tableOrSubqueryList.stream()
                                .map(SqlTableOrSubquery::getTableName)
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList());
                    })
                    .flatMap(Collection::stream)
                    .map(it -> {
                        TextRange textRange = it.getTextRange().shiftLeft(startOffsetInParent);
                        return new TableReference(sqlStatement, it, textRange);
                    })
                    .collect(Collectors.toList());
        }

        private List<CommonReference> createTableAliasReferences(SqlStatement sqlStatement,
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
                return Collections.emptyList();
            }

            return sqlTableNames.stream()
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