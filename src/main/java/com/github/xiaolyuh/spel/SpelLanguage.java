package com.github.xiaolyuh.spel;

import com.intellij.lang.Language;

/**
 * 第一步:定义语言
 */
public class SpelLanguage extends Language {

    public static final SpelLanguage INSTANCE = new SpelLanguage();

    private SpelLanguage() {
        super("SpEL");
    }

}
