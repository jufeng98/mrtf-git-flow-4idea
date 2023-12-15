package com.github.xiaolyuh.vo;

public class InstanceVo {
    private String name;
    private String desc;
    private boolean previews;

    public InstanceVo(String name, String desc, boolean previews) {
        this.name = name;
        this.desc = desc;
        this.previews = previews;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isPreviews() {
        return previews;
    }

    public void setPreviews(boolean previews) {
        this.previews = previews;
    }
}
