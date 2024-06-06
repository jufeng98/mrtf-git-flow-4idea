package com.github.xiaolyuh.pcel.psi;

import com.github.xiaolyuh.cls.psi.SimpleTypes;
import com.intellij.psi.tree.TokenSet;

public class PointcutExpressionTokenSets {
    TokenSet EXPR = TokenSet.create(PointcutExpressionTypes.EXPR);

    TokenSet ANNOTATION = TokenSet.create(PointcutExpressionTypes.ANNOTATION);
}
