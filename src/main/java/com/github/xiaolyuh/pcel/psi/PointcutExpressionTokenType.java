package com.github.xiaolyuh.pcel.psi;

import com.github.xiaolyuh.pcel.PointcutExpressionLanguage;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * 第五步:定义标识类型
 */
public class PointcutExpressionTokenType extends IElementType {

    public PointcutExpressionTokenType(@NotNull @NonNls String debugName) {
        super(debugName, PointcutExpressionLanguage.INSTANCE);
    }

    @Override
    public String toString() {
        return "PointcutExpressionTokenType." + super.toString();
    }

}
