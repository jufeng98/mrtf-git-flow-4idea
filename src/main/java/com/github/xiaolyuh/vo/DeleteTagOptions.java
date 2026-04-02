package com.github.xiaolyuh.vo;

import java.util.List;

public class DeleteTagOptions {
    private List<TagVo> tags;

    @Override
    public String toString() {
        return "DeleteBranchOptions{" +
                "branches=" + tags +
                '}';
    }

    public DeleteTagOptions() {
    }

    public List<TagVo> getTags() {
        return this.tags;
    }

    public void setTags(List<TagVo> tags) {
        this.tags = tags;
    }

}
