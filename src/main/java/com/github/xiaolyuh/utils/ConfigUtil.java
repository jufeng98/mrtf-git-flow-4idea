package com.github.xiaolyuh.utils;

import com.github.xiaolyuh.config.InitOptions;
import com.github.xiaolyuh.config.K8sOptions;
import com.github.xiaolyuh.consts.Constants;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import kotlin.Pair;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
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
    private static final Map<String, InitOptions> CONFIG_MAP = new ConcurrentHashMap<>();
    private static final Map<String, K8sOptions> K8S_MAP = new ConcurrentHashMap<>();

    public static Pair<String, String> getBaiduConfig() {
        return new Pair<>(PREFERENCES.get("baiduAppId", ""), PREFERENCES.get("baiduAppKey", ""));
    }

    public static void saveBaiduConfig(String appId, String appKey) {
        PREFERENCES.put("baiduAppId", appId);
        PREFERENCES.put("baiduAppKey", appKey);
    }

    public static Pair<String, String> getKubesphereUser() {
        return new Pair<>(PREFERENCES.get("kubesphereUsername", ""), PREFERENCES.get("kubespherePassword", ""));
    }

    public static void saveKubesphereUser(String name, String pwd, String kubesphereTokenGroup, Project project) {
        PREFERENCES.put("kubesphereUsername", name);
        PREFERENCES.put("kubespherePassword", pwd);
        PREFERENCES.put("kubesphereTokenGroup:" + project.getName(), kubesphereTokenGroup);
    }

    public static boolean isUseKubesphereTokenGroup(Project project) {
        return StringUtils.isNotBlank(getKubesphereTokenGroup(project));
    }

    public static String getKubesphereTokenGroup(Project project) {
        return PREFERENCES.get("kubesphereTokenGroup:" + project.getName(), "");
    }

    public static String getKubesphereToken(Project project) {
        String kubesphereTokenGroup = getKubesphereTokenGroup(project);
        if (StringUtils.isNotBlank(kubesphereTokenGroup)) {
            return kubesphereTokenGroup;
        }

        return PREFERENCES.get("kubesphereToken", "abcd");
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
        CONFIG_MAP.remove(project.getBasePath() + File.separator + Constants.CONFIG_FILE_NAME);
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
        CONFIG_MAP.remove(project.getBasePath() + File.separator + Constants.CONFIG_FILE_NAME);

        K8S_MAP.remove(project.getBasePath() + File.separator + Constants.CONFIG_FILE_NAME_PROJECT);
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
            options.setKubesphereTokenGroup(getKubesphereTokenGroup(project));
            CONFIG_MAP.put(project.getBasePath() + File.separator + Constants.CONFIG_FILE_NAME, options);
        }

        K8sOptions k8sOptions = getFromProjectK8sFile(project);
        if (k8sOptions != null) {
            K8S_MAP.put(project.getBasePath() + File.separator + Constants.CONFIG_FILE_NAME_PROJECT, k8sOptions);
        }
    }

    public static @NotNull InitOptions getInitOptions(Project project) {
        return CONFIG_MAP.get(project.getBasePath() + File.separator + Constants.CONFIG_FILE_NAME);
    }

    /**
     * 获取配置
     *
     * @param project project
     * @return InitOptions
     */
    public static Optional<InitOptions> getConfig(@NotNull Project project) {
        InitOptions initOptions = CONFIG_MAP.get(project.getBasePath() + File.separator + Constants.CONFIG_FILE_NAME);
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
                LOG.info(filePath + "不存在");
                return null;
            }
            String config = FileUtils.readFileToString(file, StandardCharsets.UTF_8.name());
            LOG.info("完成读取配置文件:" + filePath);
            return HttpClientUtil.gson.fromJson(config, InitOptions.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean existsK8sOptions(Project project) {
        if (project == null) {
            LOG.warn("project为null");
            return false;
        }
        K8sOptions k8sOptions = getK8sOptions(project);
        return k8sOptions != null;
    }

    public static boolean notExistsK8sOptions(Project project) {
        return !existsK8sOptions(project);
    }

    public static String getConsoleUrl(@Nullable Project project, String serviceName, String instanceName) {
        K8sOptions k8sOptions = getK8sOptions(project);
        String consoleUrl = k8sOptions.getHost() + k8sOptions.getConsolePath();
        return MessageFormat.format(consoleUrl, k8sOptions.getCluster(), k8sOptions.getNamespace(), instanceName, serviceName);
    }

    public static String getRunsUrl(Project project) {
        InitOptions initOptions = ConfigUtil.getInitOptions(project);
        K8sOptions k8sOptions = getK8sOptions(project);
        String runsUrl = k8sOptions.getHost() + k8sOptions.getRunsPath();

        String testBranch = URLEncoder.encode(initOptions.getTestBranch(), StandardCharsets.UTF_8);
        testBranch = URLEncoder.encode(testBranch, StandardCharsets.UTF_8);
        String pipelines = k8sOptions.getPipelines();
        String str = StringUtils.isNotBlank(pipelines) ? pipelines : k8sOptions.getNamespace();

        return MessageFormat.format(runsUrl, k8sOptions.getCluster(), str, testBranch);
    }

    public static String getCompileLogPath(Project project) {
        K8sOptions k8sOptions = getK8sOptions(project);
        return k8sOptions.getCompileLogPath();
    }

    public static String getPodsUrl(Project project, String serviceName) {
        K8sOptions k8sOptions = getK8sOptions(project);
        String podsUrl = k8sOptions.getHost() + k8sOptions.getPodsPath();
        return MessageFormat.format(podsUrl, k8sOptions.getCluster(), k8sOptions.getNamespace(), serviceName.toLowerCase());
    }

    public static String getLogsUrl(Project project, String serviceName, String instanceName, int tailLines,
                                    boolean previous, boolean follow) {
        K8sOptions k8sOptions = getK8sOptions(project);
        String logsUrl = k8sOptions.getHost() + k8sOptions.getLogsPath();
        return MessageFormat.format(logsUrl, k8sOptions.getCluster(), k8sOptions.getNamespace(), instanceName,
                serviceName.toLowerCase(), tailLines + "", follow, previous);
    }

    public static String getCrumbissuerUrl(Project project) {
        K8sOptions k8sOptions = getK8sOptions(project);
        String podsUrl = k8sOptions.getHost() + k8sOptions.getCrumbissuerPath();
        return MessageFormat.format(podsUrl, k8sOptions.getCluster());
    }

    public static K8sOptions getK8sOptions(@Nullable Project project) {
        if (project == null) {
            LOG.error("project为null");
            return new K8sOptions();
        }
        return K8S_MAP.get(project.getBasePath() + File.separator + Constants.CONFIG_FILE_NAME_PROJECT);
    }

    public static String getLoginUrl(Project project) {
        K8sOptions k8sOptions = K8S_MAP.get(project.getBasePath() + File.separator + Constants.CONFIG_FILE_NAME_PROJECT);
        return k8sOptions.getLoginUrl();
    }

    public static String getApolloUrl(Project project) {
        K8sOptions k8sOptions = K8S_MAP.get(project.getBasePath() + File.separator + Constants.CONFIG_FILE_NAME_PROJECT);
        return k8sOptions.getApolloUrl();
    }

    private static K8sOptions getFromProjectK8sFile(Project project) {
        try {
            String filePath = project.getBasePath() + File.separator + Constants.CONFIG_FILE_NAME_PROJECT;
            File file = new File(filePath);
            if (!file.exists()) {
                LOG.info(filePath + "不存在");
                return null;
            }
            String config = FileUtils.readFileToString(file, StandardCharsets.UTF_8.name());
            return HttpClientUtil.gson.fromJson(config, K8sOptions.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
