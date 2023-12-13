package com.github.xiaolyuh;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author yuhao.wang3
 * @since 2020/4/7 20:20
 */
public enum LanguageEnum {
    CN("中文", Locale.CHINESE),
    EN("English", Locale.ENGLISH);

    private final String language;
    private final Locale locale;

    LanguageEnum(String language, Locale locale) {
        this.language = language;
        this.locale = locale;
    }

    public static LanguageEnum getByLanguage(String language) {
        for (LanguageEnum anEnum : values()) {
            if (anEnum.getLanguage().equals(language)) {
                return anEnum;
            }
        }
        return CN;
    }

    public Locale getLocale() {
        return locale;
    }

    public static List<String> getAllLanguage() {
        List<String> languages = new ArrayList<>();
        for (LanguageEnum anEnum : values()) {
            languages.add(anEnum.getLanguage());
        }
        return languages;
    }

    public String getLanguage() {
        return language;
    }

}
