package com.github.xiaolyuh.vo;

/**
 * merge request参数
 *
 * @author yuhao.wang3
 */
public class MergeRequestOptions {

    /**
     * 标题
     */
    private String title;

    /**
     * 描述
     */
    private String message;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        String[] messages = message.split("\n");
        for (String msg:messages) {
            sb.append("<p>");
            sb.append(msg);
            sb.append("</p>");
        }
        return sb.toString();
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
