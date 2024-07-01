package com.github.xiaolyuh.spring;

import com.github.xiaolyuh.utils.ValueUtils;
import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.lang.properties.IProperty;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.JavaRecursiveElementWalkingVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


final class ValueAnnotationFoldingBuilder extends FoldingBuilderEx {

    @Override
    public FoldingDescriptor @NotNull [] buildFoldRegions(@NotNull PsiElement root, @NotNull Document document, boolean quick) {
        FoldingGroup group = FoldingGroup.newGroup(ValueUtils.DOLLAR_START);
        List<FoldingDescriptor> descriptors = new ArrayList<>();

        root.accept(new JavaRecursiveElementWalkingVisitor() {

            @Override
            public void visitLiteralExpression(@NotNull PsiLiteralExpression psiLiteralExpression) {
                super.visitLiteralExpression(psiLiteralExpression);

                Triple<List<String>, TextRange, List<PsiElement>> triple = ValueUtils.findApolloConfig(psiLiteralExpression);
                if (triple == null) {
                    return;
                }

                TextRange literalTextRange = psiLiteralExpression.getTextRange();
                TextRange textRange = new TextRange(literalTextRange.getStartOffset() + 1, literalTextRange.getEndOffset() - 1);
                List<PsiElement> psiElements = triple.getRight();

                descriptors.add(new FoldingDescriptor(psiLiteralExpression.getNode(), textRange, group, new HashSet<>(psiElements)));
            }
        });

        return descriptors.toArray(FoldingDescriptor.EMPTY);
    }

    @Override
    public String getPlaceholderText(@NotNull ASTNode node) {
        PsiElement psiElement = node.getPsi();

        Triple<List<String>, TextRange, List<PsiElement>> triple = ValueUtils.findApolloConfig(psiElement);
        if (triple == null) {
            return "";
        }

        List<PsiElement> targetElements = triple.getRight();

        IProperty prop = (IProperty) targetElements.get(0);
        return prop.getValue();
    }

    @Override
    public boolean isCollapsedByDefault(@NotNull ASTNode node) {
        return true;
    }

}
