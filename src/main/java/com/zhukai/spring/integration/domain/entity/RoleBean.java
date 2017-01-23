package com.zhukai.spring.integration.domain.entity;

import com.zhukai.spring.integration.commons.annotation.Entity;
import com.zhukai.spring.integration.commons.annotation.GeneratedValue;
import com.zhukai.spring.integration.commons.annotation.Id;

/**
 * Created by zhukai on 17-1-20.
 */
@Entity
public class RoleBean {
    @Id
    @GeneratedValue
    private Integer id;

    private String roleName;

    private Integer level;

    public RoleBean() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "RoleBean{" +
                "id=" + id +
                ", roleName='" + roleName + '\'' +
                ", level=" + level +
                '}';
    }
}
