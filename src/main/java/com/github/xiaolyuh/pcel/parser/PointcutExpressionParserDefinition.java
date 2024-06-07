package com.github.xiaolyuh.pcel.parser;

import com.github.xiaolyuh.pcel.PointcutExpressionLanguage;
import com.github.xiaolyuh.pcel.psi.PointcutExpressionTypes;
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

/**
 * 第十二步:定义解析器,并在plugin.xml里注册
 */
public class PointcutExpressionParserDefinition implements ParserDefinition {
    public static final IFileElementType FILE = new IFileElementType(PointcutExpressionLanguage.INSTANCE);

    @Override
    public @NotNull Lexer createLexer(Project project) {
        return new PointcutExpressionAdapter();
    }

    @Override
    public @NotNull PsiParser createParser(Project project) {
        return new PointcutExpressionParser();
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
        return PointcutExpressionTypes.Factory.createElement(node);
    }

    @Override
    public @NotNull PsiFile createFile(@NotNull FileViewProvider viewProvider) {
        return new PointcutExpressionFile(viewProvider);
    }
}
