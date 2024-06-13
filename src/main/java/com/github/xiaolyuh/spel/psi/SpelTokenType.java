package com.github.xiaolyuh.spel.psi;

import com.github.xiaolyuh.spel.SpelLanguage;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * 第五步:定义标识类型
 */
public class SpelTokenType extends IElementType {

    public SpelTokenType(@NotNull @NonNls String debugName) {
        super(debugName, SpelLanguage.INSTANCE);
    }

    @Override
    public String toString() {
        return SpelTokenType.class.getSimpleName() + "." + super.toString();
    }

}
