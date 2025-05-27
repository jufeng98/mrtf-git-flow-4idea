package com.github.xiaolyuh.i18n;

import com.github.xiaolyuh.enums.LanguageEnum;
import org.jetbrains.annotations.PropertyKey;

import static com.github.xiaolyuh.i18n.UiBundle.BUNDLE;

/**
 * 国际化
 *
 * @author yuhao.wang3
 * @since 2020/4/7 19:39
 */
public class I18n {

    public static String getContent(@PropertyKey(resourceBundle = BUNDLE) String key) {
        return UiBundle.INSTANCE.message(key);
    }

    public static String nls(@PropertyKey(resourceBundle = BUNDLE) String key) {
        return UiBundle.INSTANCE.message(key);
    }

    public static String getContent(@PropertyKey(resourceBundle = BUNDLE) String key, LanguageEnum languageEnum) {
        return UiBundle.INSTANCE.message(key, languageEnum);
    }

    public static String getContent(@PropertyKey(resourceBundle = BUNDLE) String key, Object... params) {
        return UiBundle.INSTANCE.message(key, params);
    }

    public static String nls(@PropertyKey(resourceBundle = BUNDLE) String key, Object... params) {
        return UiBundle.INSTANCE.message(key, params);
    }

}
