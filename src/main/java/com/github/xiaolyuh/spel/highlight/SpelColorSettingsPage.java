package com.github.xiaolyuh.spel.highlight;

import com.github.xiaolyuh.cls.step5.SimpleSyntaxHighlighter;
import com.github.xiaolyuh.spel.SpelIcons;
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
public class SpelColorSettingsPage implements ColorSettingsPage {

    @SuppressWarnings("DialogTitleCapitalization")
    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("标识符", SpelSyntaxHighlighter.IDENTIFIER),
            new AttributesDescriptor("数字", SpelSyntaxHighlighter.NUMBER),
            new AttributesDescriptor("字符串", SpelSyntaxHighlighter.STRING),
            new AttributesDescriptor("Bad value", SimpleSyntaxHighlighter.BAD_CHARACTER)
    };

    @Override
    public Icon getIcon() {
        return SpelIcons.FILE;
    }

    @NotNull
    @Override
    public SyntaxHighlighter getHighlighter() {
        return new SpelSyntaxHighlighter();
    }

    @NotNull
    @Override
    public String getDemoText() {
        return "#reqVo.backOrder.backOrderCode";
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
        return "SpEL";
    }

}
