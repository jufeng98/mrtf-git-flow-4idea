package com.github.xiaolyuh.i18n;

import com.github.xiaolyuh.enums.LanguageEnum;
import com.github.xiaolyuh.utils.ConfigUtil;
import com.intellij.BundleBase;
import com.intellij.DynamicBundle;
import com.intellij.ide.impl.ProjectUtil;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

import java.util.ResourceBundle;

public class UiBundle extends DynamicBundle {
    public static final String BUNDLE = "messages.ui";
    private static final UiBundle INSTANCE = new UiBundle(BUNDLE);

    public UiBundle(@NotNull String pathToBundle) {
        super(pathToBundle);
    }

    @Override
    public @NotNull @Nls String getMessage(@NotNull @NonNls String key, Object @NotNull ... params) {
        Project activeProject = ProjectUtil.getActiveProject();
        LanguageEnum language;
        if (activeProject != null) {
            language = ConfigUtil.getInitOptions(activeProject).getLanguage();
        } else {
            language = LanguageEnum.CN;
        }
        return message(key, language, params);
    }

    public static @Nls String message(@NotNull @PropertyKey(resourceBundle = BUNDLE) String key, Object @NotNull ... params) {
        return INSTANCE.getMessage(key, params);
    }

    public static @Nls String message(@NotNull @PropertyKey(resourceBundle = BUNDLE) String key, LanguageEnum language,
                                      Object @NotNull ... params) {
        return BundleBase.messageOrDefault(ResourceBundle.getBundle(BUNDLE, language.getLocale()), key, null, params);
    }

}
