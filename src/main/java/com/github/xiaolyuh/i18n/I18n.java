package com.github.xiaolyuh.i18n;

import com.github.xiaolyuh.InitOptions;
import com.github.xiaolyuh.LanguageEnum;
import com.github.xiaolyuh.utils.ConfigUtil;
import com.intellij.openapi.project.Project;

import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

/**
 * 国际化
 *
 * @author yuhao.wang3
 * @since 2020/4/7 19:39
 */
public class I18n {
    private static volatile Properties properties;
    private static volatile Project project;

    public static void init(Project project) {
        if (Objects.nonNull(I18n.properties)) {
            return;
        }
        synchronized (I18n.class) {
            if (Objects.isNull(I18n.properties)) {
                if (Objects.isNull(project)) {
                    loadLanguageProperties(LanguageEnum.CN);
                }
                if (Objects.isNull(I18n.project) && Objects.nonNull(project)) {
                    I18n.project = project;
                    loadLanguageProperties(ConfigUtil.getConfig(project).orElse(new InitOptions()).getLanguage());
                }
            }
        }
    }

    public static void loadLanguageProperties(LanguageEnum language) {
        try {
            String fileName = language.getFile();
            // 加载资源文件
            try (InputStream in = I18n.class.getClassLoader().getResourceAsStream(fileName)) {
                properties = new Properties();
                properties.load(in);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getContent(String key) {
        if (Objects.isNull(properties)) {
            throw new RuntimeException("没有初始化配置");
        }
        return properties.getProperty(key);
    }

}
