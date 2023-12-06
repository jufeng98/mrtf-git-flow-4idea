package com.github.xiaolyuh.vo;

import java.util.List;

public class DeleteBranchOptions {
    private List<BranchVo> branches;
    private boolean isDeleteLocalBranch;

    @Override
    public String toString() {
        return "DeleteBranchOptions{" +
                "branches=" + branches +
                ", isDeleteLocalBranch=" + isDeleteLocalBranch +
                '}';
    }

    public DeleteBranchOptions() {
    }

    public List<BranchVo> getBranches() {
        return this.branches;
    }

    public void setBranches(List<BranchVo> branches) {
        this.branches = branches;
    }

    public boolean isDeleteLocalBranch() {
        return this.isDeleteLocalBranch;
    }

    public void setDeleteLocalBranch(boolean deleteLocalBranch) {
        this.isDeleteLocalBranch = deleteLocalBranch;
    }
}
