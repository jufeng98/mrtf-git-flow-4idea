package com.github.xiaolyuh.spring;

import com.github.xiaolyuh.pcel.psi.AopExpr;
import com.github.xiaolyuh.pcel.psi.PointcutExpressionElementFactory;
import com.github.xiaolyuh.utils.AopUtils;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author yudong
 */
public class AopExprManipulator extends AbstractElementManipulator<AopExpr> {

    @Override
    public @Nullable AopExpr handleContentChange(@NotNull AopExpr element, @NotNull TextRange range, String newContent)
            throws IncorrectOperationException {
        if (AopUtils.isInAtAnnotation(element)) {
            return handlePackageOrClassNameChange(element, range, newContent);
        }

        throw new UnsupportedOperationException();
    }

    /**
     * 当包名或类名发生变化,就创建新的表达式
     */
    private AopExpr handlePackageOrClassNameChange(AopExpr oldAopExpr, TextRange range, String newContent) {
        String expr = oldAopExpr.getText();
        String newExpr = expr.substring(0, range.getStartOffset()) + newContent + expr.substring(range.getEndOffset());
        AopExpr aopExpr = PointcutExpressionElementFactory.createAtAnnoExpr(oldAopExpr.getProject(), newExpr);
        return (AopExpr) oldAopExpr.replace(aopExpr);
    }

}
