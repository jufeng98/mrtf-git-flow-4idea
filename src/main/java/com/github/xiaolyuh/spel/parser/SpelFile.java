package com.github.xiaolyuh.spel.parser;

import com.github.xiaolyuh.spel.SpelFileType;
import com.github.xiaolyuh.spel.SpelLanguage;
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;

/**
 * 第十一步:定义根文件
 */
public class SpelFile extends PsiFileBase {

    public SpelFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, SpelLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return SpelFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "Spring Expression File";
    }

}
