package com.github.xiaolyuh.consts;

/**
 * 常量
 *
 * @author yuhao.wang3
 * @since 2020/3/18 9:52
 */
public abstract class Constants {
    /**
     * 本地缓存前缀
     */
    public static final String KEY_PREFIX = "git-flow-plus:";

    /**
     * 配置文件名称
     */
    public static final String CONFIG_FILE_NAME = "git-flow-plus.config";
    public static final String CONFIG_FILE_NAME_PROJECT = "git-flow-k8s.json";

    /**
     * 加锁分支名称
     */
    public static final String LOCK_BRANCH_NAME = "GFP_LOCK_BRANCH_NAME";
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String DATE_PATTERN_SHORT = "yyyyMMdd";
}
