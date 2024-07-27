package com.github.xiaolyuh.sql.completion;

import com.dbn.cache.CacheDbTable;
import com.dbn.common.util.Naming;
import com.github.xiaolyuh.dbn.DbnToolWindowPsiElement;
import com.github.xiaolyuh.sql.parser.SqlFile;
import com.github.xiaolyuh.sql.psi.SqlColumnName;
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
import java.util.Map;


public class SqlCompletionContributor extends CompletionContributor {
    private static final String[] SQL_TYPE = {"select", "update", "delete from", "insert into"};

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        PsiElement position = parameters.getPosition();
        PsiElement parent = position.getParent();

        if (parent.getParent() instanceof SqlFile) {
            fillSqlTypes(result);
            return;
        }

        if (parent instanceof SqlColumnName) {
            SqlColumnName sqlColumnName = (SqlColumnName) parent;
            fillTableAliases(result, sqlColumnName);
            return;
        }

        if (parent instanceof SqlTableAlias) {
            SqlTableAlias sqlTableAlias = (SqlTableAlias) parent;
            fillTableAliasOfGenerated(result, sqlTableAlias);
        }

    }

    private void fillTableAliasOfGenerated(CompletionResultSet result, SqlTableAlias sqlTableAlias) {
        SqlTableName sqlTableName = SqlUtils.getTableNameOfAlias(sqlTableAlias);
        if (sqlTableName == null) {
            return;
        }

        String aliasName = Naming.createAliasName(sqlTableName.getText());
        LookupElementBuilder builder = LookupElementBuilder
                .create(aliasName)
                .bold();
        result.addElement(builder);
    }

    private void fillTableAliases(CompletionResultSet result, SqlColumnName sqlColumnName) {
        SqlStatement sqlStatement = PsiTreeUtil.getParentOfType(sqlColumnName, SqlStatement.class);
        if (sqlStatement == null) {
            return;
        }

        SqlTableName columnTableAlias = SqlUtils.getTableAliasNameOfColumn(sqlColumnName);
        if (columnTableAlias != null) {
            return;
        }

        Map<String, CacheDbTable> tableMap = DbnToolWindowPsiElement.Companion.getFirstConnCacheDbTables(sqlColumnName.getProject());

        Collection<SqlTableAlias> sqlTableAliases = PsiTreeUtil.findChildrenOfType(sqlStatement, SqlTableAlias.class);
        sqlTableAliases.forEach(sqlTableAlias -> {
            String typeName = getAliasDesc(sqlTableAlias, tableMap);

            LookupElementBuilder builder = LookupElementBuilder.create(sqlTableAlias.getText())
                    .withInsertHandler((context, item) -> {
                        Editor editor = context.getEditor();
                        Document document = editor.getDocument();
                        context.commitDocument();
                        document.insertString(context.getTailOffset(), "");
                        editor.getCaretModel().moveToOffset(context.getTailOffset());
                    })
                    .withTypeText(typeName, true)
                    .bold();
            result.addElement(builder);
        });
    }

    private String getAliasDesc(SqlTableAlias sqlTableAlias, Map<String, CacheDbTable> tableMap) {
        SqlTableName sqlTableName = SqlUtils.getTableNameOfAlias(sqlTableAlias);
        if (sqlTableName == null) {
            return "";
        }

        String tableName = sqlTableName.getText();
        if (tableMap == null) {
            return tableName;
        }

        String typeName = tableName;
        CacheDbTable cacheDbTable = tableMap.get(tableName);
        if (cacheDbTable != null) {
            typeName += "(" + cacheDbTable.getComment() + ")";
        }
        return typeName;
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
