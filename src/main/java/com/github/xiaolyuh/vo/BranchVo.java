package com.github.xiaolyuh.vo;

import java.util.Date;
import java.util.Objects;

public class BranchVo {
    private Integer id;
    private Date lastCommitDate;
    private String branch;
    private String createUser;

    @Override
    public String toString() {
        return "BranchVo{" +
                "id=" + id +
                ", lastCommitDate=" + lastCommitDate +
                ", branch='" + branch + '\'' +
                ", createUser='" + createUser + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BranchVo branchVo = (BranchVo) o;
        return Objects.equals(id, branchVo.id) && Objects.equals(lastCommitDate, branchVo.lastCommitDate)
                && Objects.equals(branch, branchVo.branch) && Objects.equals(createUser, branchVo.createUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lastCommitDate, branch, createUser);
    }

    public BranchVo() {
    }

    public Date getLastCommitDate() {
        return this.lastCommitDate;
    }

    public void setLastCommitDate(Date lastCommitDate) {
        this.lastCommitDate = lastCommitDate;
    }

    public String getBranch() {
        return this.branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getCreateUser() {
        return this.createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}
