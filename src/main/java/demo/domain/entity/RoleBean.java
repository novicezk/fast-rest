package demo.domain.entity;

import com.zhukai.spring.integration.commons.annotation.*;

/**
 * Created by zhukai on 17-1-20.
 */
@Entity(name = "ROLE", indexes = {@Index(columns = {"roleName", "level"}, unique = true)})
public class RoleBean {
    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Integer id;

    @Column(name = "ROLE_NAME", length = 64)
    private String roleName;

    @Column(name = "LEVEL")
    private Integer level;

    public RoleBean() {
    }

    public RoleBean(String roleName, Integer level) {
        this.roleName = roleName;
        this.level = level;
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
