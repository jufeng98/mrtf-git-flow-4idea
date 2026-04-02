package com.github.xiaolyuh.vo;

import java.util.Date;
import java.util.Objects;

public class TagVo {
    private Integer id;
    private Date createDate;
    private String tag;
    private String createUser;

    @Override
    public String toString() {
        return "TagVo{" +
                "id=" + id +
                ", createDate=" + createDate +
                ", tag='" + tag + '\'' +
                ", createUser='" + createUser + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TagVo branchVo = (TagVo) o;
        return Objects.equals(id, branchVo.id) && Objects.equals(createDate, branchVo.createDate)
                && Objects.equals(tag, branchVo.tag) && Objects.equals(createUser, branchVo.createUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createDate, tag, createUser);
    }

    public TagVo() {
    }

    public Date getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
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
