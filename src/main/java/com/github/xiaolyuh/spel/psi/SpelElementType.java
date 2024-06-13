package com.github.xiaolyuh.spel.psi;

import com.github.xiaolyuh.spel.SpelLanguage;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * 第六步:定义元素类型
 */
public class SpelElementType extends IElementType {

    public SpelElementType(@NotNull @NonNls String debugName) {
        super(debugName, SpelLanguage.INSTANCE);
    }

}
