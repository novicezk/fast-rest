package demo.domain.entity;

import com.zhukai.spring.integration.annotation.jpa.Column;
import com.zhukai.spring.integration.annotation.jpa.Entity;
import com.zhukai.spring.integration.annotation.jpa.GeneratedValue;
import com.zhukai.spring.integration.annotation.jpa.Id;

/**
 * Created by zhukai on 17-1-18.
 */
@Entity(name = "USER")
public class UserBean {

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Integer id;

    @Column(name = "USERNAME", unique = true, nullable = false)
    private String username;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "MONEY")
    private Float money = 0.0f;

    @Column(name = "ROLE_ID")
    private RoleBean role;

    public UserBean() {
    }

    public UserBean(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public RoleBean getRole() {
        return role;
    }

    public void setRole(RoleBean role) {
        this.role = role;
    }

    public Float getMoney() {
        return money;
    }

    public void setMoney(Float money) {
        this.money = money;
    }

    @Override
    public String toString() {
        return "UserBean{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", money=" + money +
                ", role=" + role +
                '}';
    }
}
