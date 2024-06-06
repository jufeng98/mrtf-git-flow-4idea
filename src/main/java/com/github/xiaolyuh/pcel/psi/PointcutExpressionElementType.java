package com.github.xiaolyuh.pcel.psi;

import com.github.xiaolyuh.pcel.PointcutExpressionLanguage;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class PointcutExpressionElementType extends IElementType {

    public PointcutExpressionElementType(@NotNull @NonNls String debugName) {
        super(debugName, PointcutExpressionLanguage.INSTANCE);
    }

}
