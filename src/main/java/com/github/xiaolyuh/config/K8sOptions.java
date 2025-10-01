package com.github.xiaolyuh.config;

import java.util.List;

@SuppressWarnings("unused")
public class K8sOptions {
    private String apolloUrl;
    private String loginUrl;
    private String loginUrlGroup;
    private String host;
    private String namespace;
    private String namespaceSec;
    private String pipeline;
    private String pipelineSec;
    private String cluster;
    private String devop;

    private String runsUrl;
    private String runsUrlSec;
    private String crumbissuerUrl;
    private String compileLogUrl;
    private String compileLogUrlSec;
    private String pushLogUrl;
    private String pushLogUrlSec;
    private String podsUrl;
    private String podsUrlSec;
    private String logsUrl;
    private String logsUrlSec;
    private String consoleUrl;
    private String consoleUrlSec;

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

    public String getNamespaceSec() {
        return namespaceSec;
    }

    public String getPipeline() {
        return pipeline;
    }

    public String getPipelineSec() {
        return pipelineSec;
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

    public String getRunsUrlSec() {
        return runsUrlSec;
    }

    public String getCrumbissuerUrl() {
        return crumbissuerUrl;
    }

    public String getCompileLogUrl() {
        return compileLogUrl;
    }

    public String getCompileLogUrlSec() {
        return compileLogUrlSec;
    }

    public String getPushLogUrl() {
        return pushLogUrl;
    }

    public String getPushLogUrlSec() {
        return pushLogUrlSec;
    }

    public String getPodsUrl() {
        return podsUrl;
    }

    public String getPodsUrlSec() {
        return podsUrlSec;
    }

    public String getLogsUrl() {
        return logsUrl;
    }

    public String getLogsUrlSec() {
        return logsUrlSec;
    }

    public String getConsoleUrl() {
        return consoleUrl;
    }

    public String getConsoleUrlSec() {
        return consoleUrlSec;
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
