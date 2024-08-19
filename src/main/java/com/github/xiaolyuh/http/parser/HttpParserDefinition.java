package com.github.xiaolyuh.http.parser;

import com.github.xiaolyuh.http.HttpLanguage;
import com.github.xiaolyuh.http.psi.HttpTypes;
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

public class HttpParserDefinition implements ParserDefinition {
    public static final IFileElementType FILE = new IFileElementType(HttpLanguage.INSTANCE);

    @Override
    public @NotNull Lexer createLexer(Project project) {
        return new HttpAdapter();
    }

    @Override
    public @NotNull PsiParser createParser(Project project) {
        return new HttpParser();
    }

    @Override
    public @NotNull IFileElementType getFileNodeType() {
        return FILE;
    }

    @Override
    public @NotNull TokenSet getCommentTokens() {
        return TokenSet.create(HttpTypes.LINE_COMMENT, HttpTypes.REQUEST_COMMENT);
    }

    @Override
    public @NotNull TokenSet getStringLiteralElements() {
        return TokenSet.EMPTY;
    }

    @Override
    public @NotNull PsiElement createElement(ASTNode node) {
        return HttpTypes.Factory.createElement(node);
    }

    @Override
    public @NotNull PsiFile createFile(@NotNull FileViewProvider viewProvider) {
        return new HttpFile(viewProvider);
    }
}
