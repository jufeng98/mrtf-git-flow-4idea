package com.github.xiaolyuh.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.xiaolyuh.Constants;
import com.github.xiaolyuh.InitOptions;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import java.util.prefs.Preferences;

/**
 * 配置管理工具
 *
 * @author yuhao.wang3
 * @since 2020/3/18 11:19
 */
public class ConfigUtil {
    public static Preferences PREFERENCES = Preferences.userRoot().node("com.github.xiaolyuh");
    /**
     * 将配置存储到本地项目空间
     *
     * @param project    project
     * @param configJson configJson
     */
    public static void saveConfigToLocal(Project project, String configJson) {
        // 存储到本地项目空间
        PropertiesComponent component = PropertiesComponent.getInstance(project);
        component.setValue(Constants.KEY_PREFIX + project.getName(), configJson);
    }

    /**
     * 将配置存储到文件项目空间
     *
     * @param project    project
     * @param configJson configJson
     */
    public static void saveConfigToFile(Project project, String configJson) {
        String filePath = project.getBasePath() + File.separator + Constants.CONFIG_FILE_NAME;
        File file = new File(filePath);
        try (PrintStream ps = new PrintStream(new FileOutputStream(file))) {
            // 往文件里写入字符串
            ps.println(configJson);
            ps.flush();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 判断插件否初始化
     *
     * @param project project
     * @return boolean 已初始化返回true，否则返回false
     */
    public static boolean isInit(Project project) {
        if (Objects.isNull(project)) {
            return false;
        }

        return getConfig(project).isPresent();
    }

    /**
     * 将配置存储到配置文件
     *
     * @param project project
     * @return InitOptions
     */
    public static Optional<InitOptions> getConfig(@NotNull Project project) {
        InitOptions options = getConfigToFile(project);
        if (Objects.isNull(options)) {
            options = getConfigToLocal(project);
        }
        if (Objects.nonNull(options)) {
            options.setKubesphereUsername(PREFERENCES.get("kubesphereUsername", ""));
            options.setKubespherePassword(PREFERENCES.get("kubespherePassword", ""));
        }
        return Optional.ofNullable(options);
    }

    /**
     * 从本地项目空间获取配置
     *
     * @param project project
     * @return InitOptions
     */
    private static InitOptions getConfigToLocal(@NotNull Project project) {
        PropertiesComponent component = PropertiesComponent.getInstance(project);
        String json = component.getValue(Constants.KEY_PREFIX + project.getName());
        if (StringUtils.isNotBlank(json)) {
            return JSON.parseObject(json, InitOptions.class);
        }
        return null;
    }

    /**
     * 从配置文件获取配置
     *
     * @param project project
     * @return InitOptions
     */
    private static InitOptions getConfigToFile(Project project) {
        try {
            String filePath = project.getBasePath() + File.separator + Constants.CONFIG_FILE_NAME;
            File file = new File(filePath);
            if (!file.exists()) {
                return null;
            }
            String config = FileUtils.readFileToString(file, StandardCharsets.UTF_8.name());
            return JSON.parseObject(config, InitOptions.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static JSONObject getProjectConfigToFile(Project project) {
        try {
            String filePath = project.getBasePath() + File.separator + Constants.CONFIG_FILE_NAME_PROJECT;
            File file = new File(filePath);
            if (!file.exists()) {
                return null;
            }
            String config = FileUtils.readFileToString(file, StandardCharsets.UTF_8.name());
            return JSON.parseObject(config, JSONObject.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
