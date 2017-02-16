package demo.common;

/**
 * Created by zcfrank1st on 3/16/16.
 */
public enum MessageCode {
    SUCCESS(-1, "接口调用成功"),
    SYSTEM_ERROR(-2, "系统异常"),
    ILLEGAL_ARGUMENT(-3, "入参错误"),
    LOGIN_ERROR(-4, "用户名或密码错误"),
    USERNAME_REPEAT(-5, "用户名重复"),
    DB_ERROR(-6, "数据库操作失败"),
    REGISTER_TASK_ERROR(-24, "注册服务失败"),
    DELETE_TASK_ERROR(-25, "调用服务失败");

    // 接口返回编码
    private int code;

    // 接口返回编码描述
    private String description;

    MessageCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
