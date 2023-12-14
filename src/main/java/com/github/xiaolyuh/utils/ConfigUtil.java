package com.github.xiaolyuh.utils;

import com.github.xiaolyuh.consts.Constants;
import com.github.xiaolyuh.config.InitOptions;
import com.google.gson.JsonObject;
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
    private static final Preferences PREFERENCES = Preferences.userRoot().node("com.github.xiaolyuh");
    private static final Map<String, InitOptions> map = new ConcurrentHashMap<>();

    public static Pair<String, String> getKubesphereUser() {
        return new Pair<>(PREFERENCES.get("kubesphereUsername", ""), PREFERENCES.get("kubespherePassword", ""));
    }

    public static void saveKubesphereUser(String name, String pwd) {
        PREFERENCES.put("kubesphereUsername", name);
        PREFERENCES.put("kubespherePassword", pwd);
    }

    public static String getKubesphereToken() {
        return PREFERENCES.get("kubesphereToken", "");
    }

    public static void saveKubesphereToken(String token) {
        PREFERENCES.put("kubesphereToken", token);
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
        if (project == null) {
            return false;
        }
        return getConfig(project).isPresent();
    }

    public static void tryInitConfig(Project project) {
        InitOptions options = getFromProjectConfigFile(project);
        if (Objects.isNull(options)) {
            options = getFromProjectWorkspace(project);
        }

        if (Objects.nonNull(options)) {
            Pair<String, String> pair = getKubesphereUser();
            options.setKubesphereUsername(pair.getFirst());
            options.setKubespherePassword(pair.getSecond());
            map.put(project.getBasePath() + File.separator + Constants.CONFIG_FILE_NAME, options);
        }
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
        return Optional.ofNullable(initOptions);
    }

    /**
     * 从本地项目空间获取配置
     *
     * @param project project
     * @return InitOptions
     */
    private static InitOptions getFromProjectWorkspace(@NotNull Project project) {
        PropertiesComponent component = PropertiesComponent.getInstance(project);
        String key = Constants.KEY_PREFIX + project.getName();
        String json = component.getValue(key);
        if (StringUtils.isNotBlank(json)) {
            LOG.info("完成读取项目空间配置workspace.xml,key:" + key);
            return HttpClientUtil.gson.fromJson(json, InitOptions.class);
        }
        return null;
    }

    /**
     * 从配置文件获取配置
     *
     * @param project project
     * @return InitOptions
     */
    private static InitOptions getFromProjectConfigFile(Project project) {
        String filePath = project.getBasePath() + File.separator + Constants.CONFIG_FILE_NAME;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return null;
            }
            String config = FileUtils.readFileToString(file, StandardCharsets.UTF_8.name());
            LOG.info("完成读取配置文件:" + filePath);
            return HttpClientUtil.gson.fromJson(config, InitOptions.class);
        } catch (Exception e) {
            NotifyUtil.notifyError(project, "读取" + filePath + "错误:" + e.getClass().getSimpleName() + "," + e.getMessage());
            return null;
        }
    }

    public static JsonObject getProjectConfigToFile(Project project) {
        try {
            String filePath = project.getBasePath() + File.separator + Constants.CONFIG_FILE_NAME_PROJECT;
            File file = new File(filePath);
            if (!file.exists()) {
                return null;
            }
            String config = FileUtils.readFileToString(file, StandardCharsets.UTF_8.name());
            return HttpClientUtil.gson.fromJson(config, JsonObject.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
