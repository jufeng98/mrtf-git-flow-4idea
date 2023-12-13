package com.github.xiaolyuh.i18n;

import com.github.xiaolyuh.enums.LanguageEnum;

/**
 * 国际化
 *
 * @author yuhao.wang3
 * @since 2020/4/7 19:39
 */
public class I18n {

    public static String getContent(String key) {
        return UiBundle.message(key);
    }

    public static String getContent(String key, LanguageEnum languageEnum) {
        return UiBundle.message(key, languageEnum);
    }

    public static String getContent(String key, Object... params) {
        return UiBundle.message(key, params);
    }

}
