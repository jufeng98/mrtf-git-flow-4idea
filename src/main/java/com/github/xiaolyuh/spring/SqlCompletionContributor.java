package com.github.xiaolyuh.spring;

import com.dbn.object.DBTable;
import com.github.xiaolyuh.dbn.DbnToolWindowPsiElement;
import com.github.xiaolyuh.sql.parser.SqlFile;
import com.github.xiaolyuh.sql.psi.SqlColumnName;
import com.github.xiaolyuh.sql.psi.SqlJoinClause;
import com.github.xiaolyuh.sql.psi.SqlJoinConstraint;
import com.github.xiaolyuh.sql.psi.SqlResultColumn;
import com.github.xiaolyuh.sql.psi.SqlStatement;
import com.github.xiaolyuh.sql.psi.SqlTableAlias;
import com.github.xiaolyuh.sql.psi.SqlTableName;
import com.github.xiaolyuh.utils.SqlUtils;
import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public class SqlCompletionContributor extends CompletionContributor {
    private static final String[] SQL_TYPE = {"select", "update", "delete", "insert"};

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        PsiElement position = parameters.getPosition();

        if (position.getParent().getParent() instanceof SqlFile) {
            fillSqlTypes(result);
            return;
        }

        if (position.getParent() instanceof SqlTableName) {
            fillTableNames(result);
            return;
        }

        if (position.getParent() instanceof SqlColumnName) {
            SqlColumnName sqlColumnName = (SqlColumnName) position.getParent();
            fillColumnNames(result, sqlColumnName);
        }

    }

    private void fillColumnNames(CompletionResultSet result, SqlColumnName sqlColumnName) {
        List<DBTable> tables = DbnToolWindowPsiElement.Companion.getTables();
        if (tables == null) {
            return;
        }

        SqlStatement sqlStatement = PsiTreeUtil.getParentOfType(sqlColumnName, SqlStatement.class);
        if (sqlStatement == null) {
            return;
        }

        // 获取列名前面的表别名
        SqlTableName aliasTableName = PsiTreeUtil.getPrevSiblingOfType(sqlColumnName, SqlTableName.class);
        if (aliasTableName == null) {
            fillAliasName(result, sqlStatement);

            Collection<SqlJoinClause> sqlJoinClauses = PsiTreeUtil.findChildrenOfType(sqlStatement, SqlJoinClause.class);
            Set<String> sqlTableNames = SqlUtils.getSqlTableNames(sqlJoinClauses).stream()
                    .map(PsiElement::getText)
                    .collect(Collectors.toSet());
            fillColumnNames(sqlColumnName, result, sqlTableNames, tables);
            return;
        }

        Collection<SqlJoinClause> sqlJoinClauses = PsiTreeUtil.findChildrenOfType(sqlStatement, SqlJoinClause.class);
        Map<String, List<SqlTableAlias>> aliasMap = SqlUtils.getAliasMap(sqlJoinClauses);
        String aliasName = aliasTableName.getText();
        SqlTableName sqlTableName = SqlUtils.getTableNameOfAlias(aliasMap, aliasName);
        if (sqlTableName == null) {
            return;
        }

        String tableName = sqlTableName.getText();

        fillColumnNames(sqlColumnName, result, Set.of(tableName), tables);
    }

    private void fillColumnNames(SqlColumnName sqlColumnName, CompletionResultSet result, Set<String> tableNames, List<DBTable> tables) {
        String insertStr;
        if (PsiTreeUtil.getParentOfType(sqlColumnName, SqlResultColumn.class) != null) {
            insertStr = ", ";
        } else if (PsiTreeUtil.getParentOfType(sqlColumnName, SqlJoinConstraint.class) != null) {
            insertStr = " = ";
        } else {
            insertStr = "";
        }

        tables.stream()
                .filter(it -> tableNames.contains(it.getName()))
                .forEach(it -> it.getColumns()
                        .forEach(dbColumn -> {
                            LookupElementBuilder builder = LookupElementBuilder.create(dbColumn.getName())
                                    .withInsertHandler((context, item) -> {
                                        Editor editor = context.getEditor();
                                        Document document = editor.getDocument();
                                        context.commitDocument();
                                        document.insertString(context.getTailOffset(), insertStr);
                                        editor.getCaretModel().moveToOffset(context.getTailOffset());
                                    })
                                    .withTypeText(dbColumn.getDataType().getQualifiedName() + " " + dbColumn.getColumnComment(), true)
                                    .bold();
                            result.addElement(builder);
                        }));
    }

    private void fillTableNames(CompletionResultSet result) {
        List<DBTable> tables = DbnToolWindowPsiElement.Companion.getTables();
        if (tables == null) {
            return;
        }

        tables.forEach(it -> {
            LookupElementBuilder builder = LookupElementBuilder
                    .create(it.getName())
                    .withTypeText(it.getComment(), true)
                    .bold();
            result.addElement(builder);
        });
    }


    private void fillAliasName(CompletionResultSet result, SqlStatement sqlStatement) {
        Collection<SqlJoinClause> sqlJoinClauses = PsiTreeUtil.findChildrenOfType(sqlStatement, SqlJoinClause.class);
        Map<String, List<SqlTableAlias>> aliasMap = SqlUtils.getAliasMap(sqlJoinClauses);
        aliasMap.forEach((aliasName, value) -> {
            SqlTableAlias sqlTableAlias = value.get(0);
            LookupElementBuilder builder = LookupElementBuilder.create(aliasName)
                    .bold();

            SqlTableName aliasTableName = PsiTreeUtil.getPrevSiblingOfType(sqlTableAlias, SqlTableName.class);
            if (aliasTableName != null) {
                builder = builder.withTypeText(aliasTableName.getText(), true);
            }

            result.addElement(builder);
        });
    }

    private void fillSqlTypes(CompletionResultSet result) {
        for (String suggestion : SQL_TYPE) {
            LookupElementBuilder builder = LookupElementBuilder.create(suggestion)
                    .withInsertHandler((context, item) -> {
                        Editor editor = context.getEditor();
                        Document document = editor.getDocument();
                        context.commitDocument();
                        document.insertString(context.getTailOffset(), " ");
                        editor.getCaretModel().moveToOffset(context.getTailOffset());
                    })
                    .bold();
            result.addElement(builder);
        }
    }

}
