package com.github.xiaolyuh.spel;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * 第三步:定义文件类型,并在plugin.xml里注册
 */
public class SpelFileType extends LanguageFileType {

    public static final SpelFileType INSTANCE = new SpelFileType();

    private SpelFileType() {
        super(SpelLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "SpEL";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Spring expression language file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "spel";
    }

    @Override
    public Icon getIcon() {
        return SpelIcons.FILE;
    }

}
