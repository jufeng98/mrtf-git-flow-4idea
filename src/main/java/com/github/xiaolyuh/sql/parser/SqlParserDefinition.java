package com.github.xiaolyuh.sql.parser;

import com.github.xiaolyuh.spel.SpelLanguage;
import com.github.xiaolyuh.spel.parser.SpelAdapter;
import com.github.xiaolyuh.spel.parser.SpelFile;
import com.github.xiaolyuh.spel.parser.SpelParser;
import com.github.xiaolyuh.spel.psi.SpelTypes;
import com.github.xiaolyuh.sql.SqlLanguage;
import com.github.xiaolyuh.sql.psi.SqlTypes;
import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;

public class SqlParserDefinition implements ParserDefinition {
    public static final IFileElementType FILE = new IFileElementType(SqlLanguage.INSTANCE);

    @Override
    public @NotNull Lexer createLexer(Project project) {
        return new SqlAdapter();
    }

    @Override
    public @NotNull PsiParser createParser(Project project) {
        return new SqlParser();
    }

    @Override
    public @NotNull IFileElementType getFileNodeType() {
        return FILE;
    }

    @Override
    public @NotNull TokenSet getCommentTokens() {
        return TokenSet.EMPTY;
    }

    @Override
    public @NotNull TokenSet getStringLiteralElements() {
        return TokenSet.EMPTY;
    }

    @Override
    public @NotNull PsiElement createElement(ASTNode node) {
        return SqlTypes.Factory.createElement(node);
    }

    @Override
    public @NotNull PsiFile createFile(@NotNull FileViewProvider viewProvider) {
        return new SqlFile(viewProvider);
    }
}
