package demo.common;

/**
 * Created by lenovo on 2016/3/18.
 * 错误Json对象
 */
public class Message<T> {
    //错误码
    private Integer code;
    //描述
    private String description;
    //返回信息
    private T body;

    public Message(Integer code, T body, String description) {
        this.code = code;
        this.body = body;
        this.description = description;
    }

    public Message() {
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Message{" +
                "code=" + code +
                ", description='" + description + '\'' +
                ", body=" + body +
                '}';
    }
}
