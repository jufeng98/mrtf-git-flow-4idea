package com.github.xiaolyuh.config;

import java.util.List;

@SuppressWarnings("unused")
public class K8sOptions {
    private String apolloUrl;
    private String loginUrl;
    private String loginUrlGroup;
    private String host;
    private String namespace;
    private String pipeline;
    private String cluster;
    private String devop;

    private String runsUrl;
    private String crumbissuerUrl;
    private String compileLogUrl;
    private String pushLogUrl;
    private String podsUrl;
    private String logsUrl;
    private String consoleUrl;

    private String logDir;
    private String logInfoFile;
    private String logDebugFile;
    private String logErrorFile;
    private boolean isFrontProject;

    private List<String> services;

    public String getApolloUrl() {
        return apolloUrl;
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    public String getLoginUrlGroup() {
        return loginUrlGroup;
    }

    public String getHost() {
        return host;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getPipeline() {
        return pipeline;
    }

    public String getCluster() {
        return cluster;
    }

    public String getDevop() {
        return devop;
    }

    public String getRunsUrl() {
        return runsUrl;
    }

    public String getCrumbissuerUrl() {
        return crumbissuerUrl;
    }

    public String getCompileLogUrl() {
        return compileLogUrl;
    }

    public String getPushLogUrl() {
        return pushLogUrl;
    }

    public String getPodsUrl() {
        return podsUrl;
    }

    public String getLogsUrl() {
        return logsUrl;
    }

    public String getConsoleUrl() {
        return consoleUrl;
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
