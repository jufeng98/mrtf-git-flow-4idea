package com.github.xiaolyuh.pcel.highlight;

import com.github.xiaolyuh.cls.step5.SimpleSyntaxHighlighter;
import com.github.xiaolyuh.pcel.PointcutExpressionIcons;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Map;

/**
 * 第十五步:定义颜色设置页面,并在plugin.xml里注册
 */
public class PointcutExpressionColorSettingsPage implements ColorSettingsPage {

    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("Aop类型", PointcutExpressionSyntaxHighlighter.AOP_KEYWORD),
            new AttributesDescriptor("Aop表达式", PointcutExpressionSyntaxHighlighter.AOP_EXPR),
            new AttributesDescriptor("Aop方法引用", PointcutExpressionSyntaxHighlighter.AOP_KEYWORD),
            new AttributesDescriptor("Bad value", SimpleSyntaxHighlighter.BAD_CHARACTER)
    };

    @Override
    public Icon getIcon() {
        return PointcutExpressionIcons.FILE;
    }

    @NotNull
    @Override
    public SyntaxHighlighter getHighlighter() {
        return new PointcutExpressionSyntaxHighlighter();
    }

    @NotNull
    @Override
    public String getDemoText() {
        return "@annotation(org.javamaster.annos.AopLog)";
    }

    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return null;
    }

    @Override
    public AttributesDescriptor @NotNull [] getAttributeDescriptors() {
        return DESCRIPTORS;
    }

    @Override
    public ColorDescriptor @NotNull [] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "Spring PointcutExpression";
    }

}
