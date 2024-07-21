package com.github.xiaolyuh.spring;

import com.dbn.cache.CacheDbTable;
import com.dbn.common.util.Naming;
import com.github.xiaolyuh.dbn.DbnToolWindowPsiElement;
import com.github.xiaolyuh.sql.parser.SqlFile;
import com.github.xiaolyuh.sql.psi.SqlColumnName;
import com.github.xiaolyuh.sql.psi.SqlStatement;
import com.github.xiaolyuh.sql.psi.SqlTableAlias;
import com.github.xiaolyuh.sql.psi.SqlTableName;
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
    private static final String[] SQL_TYPE = {"select", "update", "delete", "insert"};

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
        SqlTableName sqlTableName = PsiTreeUtil.getPrevSiblingOfType(sqlTableAlias, SqlTableName.class);
        if (sqlTableName == null) {
            return;
        }

        String alias = Naming.createAliasName(sqlTableName.getText());
        LookupElementBuilder builder = LookupElementBuilder
                .create(alias)
                .bold();
        result.addElement(builder);
    }

    private void fillTableAliases(CompletionResultSet result, SqlColumnName sqlColumnName) {
        SqlStatement sqlStatement = PsiTreeUtil.getParentOfType(sqlColumnName, SqlStatement.class);
        if (sqlStatement == null) {
            return;
        }

        SqlTableName columnTableAliasName = PsiTreeUtil.getPrevSiblingOfType(sqlColumnName, SqlTableName.class);
        if (columnTableAliasName != null) {
            return;
        }

        Map<String, CacheDbTable> tableMap = DbnToolWindowPsiElement.Companion.getTables(sqlStatement.getProject());

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
        SqlTableName sqlTableName = PsiTreeUtil.getPrevSiblingOfType(sqlTableAlias, SqlTableName.class);
        String typeName = "";
        if (sqlTableName == null) {
            return typeName;
        }

        String tableName = sqlTableName.getText();
        typeName += tableName;
        if (tableMap == null) {
            return typeName;
        }

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
