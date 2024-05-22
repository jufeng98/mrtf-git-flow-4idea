package com.github.xiaolyuh.config;

import java.util.List;

@SuppressWarnings("unused")
public class K8sOptions {
    private String apolloUrl;
    private String loginUrl;
    private String host;
    private String namespace;
    private String cluster;
    private String runsPath;
    private String crumbissuerPath;
    private String podsPath;
    private String logsPath;
    private String logDir;
    private String logInfoFile;
    private String logDebugFile;
    private String logErrorFile;
    private String consolePath;
    private boolean isFrontProject;
    private List<String> services;

    public String getApolloUrl() {
        return apolloUrl;
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    public String getHost() {
        return host;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getCluster() {
        return cluster;
    }

    public String getRunsPath() {
        return runsPath;
    }

    public String getCrumbissuerPath() {
        return crumbissuerPath;
    }

    public String getPodsPath() {
        return podsPath;
    }

    public String getLogsPath() {
        return logsPath;
    }

    public String getConsolePath() {
        return consolePath;
    }

    public String getLogDir() {
        return logDir;
    }

    public String getLogInfoFile() {
        return logInfoFile;
    }

    public String getLogDebugFile() {
        return logDebugFile;
    }

    public String getLogErrorFile() {
        return logErrorFile;
    }

    public boolean isFrontProject() {
        return isFrontProject;
    }

    public List<String> getServices() {
        return services;
    }
}
