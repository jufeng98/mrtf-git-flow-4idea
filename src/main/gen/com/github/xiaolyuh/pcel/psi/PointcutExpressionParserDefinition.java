package com.github.xiaolyuh.pcel.psi;

import com.github.xiaolyuh.cls.SimpleLexerAdapter;
import com.github.xiaolyuh.cls.parser.SimpleParser;
import com.github.xiaolyuh.cls.psi.SimpleTypes;
import com.github.xiaolyuh.cls.step2.SimpleLanguage;
import com.github.xiaolyuh.cls.step4.SimpleFile;
import com.github.xiaolyuh.cls.step4.SimpleTokenSets;
import com.github.xiaolyuh.pcel.PointcutExpressionFileType;
import com.github.xiaolyuh.pcel.PointcutExpressionLanguage;
import com.github.xiaolyuh.pcel.laxer.PointcutExpressionLexerAdapter;
import com.github.xiaolyuh.pcel.parser.PointcutExpressionParser;
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

public class PointcutExpressionParserDefinition implements ParserDefinition {

    public static final IFileElementType FILE = new IFileElementType(PointcutExpressionLanguage.INSTANCE);

    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new PointcutExpressionLexerAdapter();
    }

    @NotNull
    @Override
    public TokenSet getCommentTokens() {
        return TokenSet.EMPTY;
    }

    @NotNull
    @Override
    public TokenSet getStringLiteralElements() {
        return TokenSet.EMPTY;
    }

    @NotNull
    @Override
    public PsiParser createParser(final Project project) {
        return new PointcutExpressionParser();
    }

    @NotNull
    @Override
    public IFileElementType getFileNodeType() {
        return FILE;
    }

    @NotNull
    @Override
    public PsiFile createFile(@NotNull FileViewProvider viewProvider) {
        return new PointcutExpressionFile(viewProvider);
    }

    @NotNull
    @Override
    public PsiElement createElement(ASTNode node) {
        return PointcutExpressionTypes.Factory.createElement(node);
    }

}
