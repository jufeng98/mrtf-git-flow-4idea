package com.github.xiaolyuh.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.xiaolyuh.Constants;
import com.github.xiaolyuh.InitOptions;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import kotlin.Pair;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.prefs.Preferences;

/**
 * 配置管理工具
 *
 * @author yuhao.wang3
 * @since 2020/3/18 11:19
 */
public class ConfigUtil {
    private static final Logger LOG = Logger.getInstance(ConfigUtil.class);

    public static Preferences PREFERENCES = Preferences.userRoot().node("com.github.xiaolyuh");
    private static final Map<String, InitOptions> map = new ConcurrentHashMap<>();

    public static Pair<String, String> getKubesphereUser() {
        return new Pair<>(PREFERENCES.get("kubesphereUsername", ""), PREFERENCES.get("kubespherePassword", ""));
    }

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
        map.remove(project.getBasePath() + File.separator + Constants.CONFIG_FILE_NAME);
    }

    /**
     * 将配置存储到本地文件
     *
     * @param project    project
     * @param configJson configJson
     */
    public static void saveConfigToFile(Project project, String configJson) {
        String filePath = project.getBasePath() + File.separator + Constants.CONFIG_FILE_NAME;
        File file = new File(filePath);
        try {
            FileUtils.write(file, configJson, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        map.remove(project.getBasePath() + File.separator + Constants.CONFIG_FILE_NAME);
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

    public static @NotNull InitOptions getInitOptions(Project project) {
        return map.get(project.getBasePath() + File.separator + Constants.CONFIG_FILE_NAME);
    }

    /**
     * 获取配置
     *
     * @param project project
     * @return InitOptions
     */
    public static Optional<InitOptions> getConfig(@NotNull Project project) {
        InitOptions initOptions = map.get(project.getBasePath() + File.separator + Constants.CONFIG_FILE_NAME);
        if (initOptions != null) {
            return Optional.of(initOptions);
        }

        InitOptions options = getConfigToFile(project);
        if (Objects.isNull(options)) {
            options = getConfigToLocal(project);
        }
        if (Objects.nonNull(options)) {
            options.setKubesphereUsername(PREFERENCES.get("kubesphereUsername", ""));
            options.setKubespherePassword(PREFERENCES.get("kubespherePassword", ""));
            map.put(project.getBasePath() + File.separator + Constants.CONFIG_FILE_NAME, options);
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
        String key = Constants.KEY_PREFIX + project.getName();
        String json = component.getValue(key);
        if (StringUtils.isNotBlank(json)) {
            LOG.info("完成读取项目空间配置workspace.xml,key:" + key);
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
        String filePath = project.getBasePath() + File.separator + Constants.CONFIG_FILE_NAME;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return null;
            }
            String config = FileUtils.readFileToString(file, StandardCharsets.UTF_8.name());
            LOG.info("完成读取配置文件:" + filePath);
            return JSON.parseObject(config, InitOptions.class);
        } catch (Exception e) {
            NotifyUtil.notifyError(project, "读取" + filePath + "错误:" + e.getClass().getSimpleName() + "," + e.getMessage());
            return null;
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
