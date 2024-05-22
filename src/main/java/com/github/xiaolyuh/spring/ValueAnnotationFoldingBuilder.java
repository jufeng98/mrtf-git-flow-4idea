package com.github.xiaolyuh.spring;

import com.github.xiaolyuh.utils.ValueUtils;
import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.JavaRecursiveElementWalkingVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
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

        @SuppressWarnings("DataFlowIssue")
        List<PsiElement> targetElements = triple.getRight();

        return getPropVal(targetElements.get(0));
    }

    private String getPropVal(PsiElement targetElement) {
        try {
            Method method = targetElement.getClass().getDeclaredMethod("getValue");
            method.setAccessible(true);
            return (String) method.invoke(targetElement);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isCollapsedByDefault(@NotNull ASTNode node) {
        return true;
    }

}
