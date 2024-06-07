package com.github.xiaolyuh.pcel.parser;

import com.github.xiaolyuh.pcel.PointcutExpressionFileType;
import com.github.xiaolyuh.pcel.PointcutExpressionLanguage;
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;

/**
 * 第十一步:定义根文件
 */
public class PointcutExpressionFile extends PsiFileBase {

    public PointcutExpressionFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, PointcutExpressionLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return PointcutExpressionFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "PointcutExpression File";
    }

}
