package com.github.xiaolyuh.cls.step4;

import com.github.xiaolyuh.cls.psi.SimpleTypes;
import com.intellij.psi.tree.TokenSet;

public interface SimpleTokenSets {

    TokenSet IDENTIFIERS = TokenSet.create(SimpleTypes.KEY);

    TokenSet COMMENTS = TokenSet.create(SimpleTypes.COMMENT);

}