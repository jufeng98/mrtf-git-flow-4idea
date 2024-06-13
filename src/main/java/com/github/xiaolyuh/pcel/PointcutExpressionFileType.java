package com.github.xiaolyuh.pcel;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * 第三步:定义文件类型
 * 第四步:在plugin.xml里注册fileType
 */
public class PointcutExpressionFileType extends LanguageFileType {

    public static final PointcutExpressionFileType INSTANCE = new PointcutExpressionFileType();

    private PointcutExpressionFileType() {
        super(PointcutExpressionLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Pointcut Expression";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Pointcut Expression language file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "pcel";
    }

    @Override
    public Icon getIcon() {
        return PointcutExpressionIcons.FILE;
    }

}
