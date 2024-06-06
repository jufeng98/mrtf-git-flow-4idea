package com.github.xiaolyuh.pcel.psi;

import com.github.xiaolyuh.cls.step2.SimpleFileType;
import com.github.xiaolyuh.cls.step2.SimpleLanguage;
import com.github.xiaolyuh.pcel.PointcutExpressionFileType;
import com.github.xiaolyuh.pcel.PointcutExpressionLanguage;
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;

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
        return "Pointcut Expression";
    }

}
