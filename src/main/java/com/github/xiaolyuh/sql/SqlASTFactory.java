package com.github.xiaolyuh.sql;

import com.github.xiaolyuh.sql.psi.SqlTypes;
import com.intellij.lang.ASTFactory;
import com.intellij.lang.DefaultASTFactory;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.impl.source.tree.LeafElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SqlASTFactory extends ASTFactory {
    private final DefaultASTFactory myDefaultASTFactory = ApplicationManager.getApplication().getService(DefaultASTFactory.class);

    @Override
    @Nullable
    public LeafElement createLeaf(@NotNull final IElementType type, @NotNull CharSequence text) {
        if (type == SqlTypes.COMMENT) {
            return myDefaultASTFactory.createComment(type, text);
        }

        return new LeafPsiElement(type, text);
    }

}
