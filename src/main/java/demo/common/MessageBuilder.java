package demo.common;


/**
 * Created by zcfrank1st on 3/16/16.
 */
public class MessageBuilder {
    public static Message build(MessageCode code) {
        Message m = new Message();
        m.setDescription(code.getDescription());
        m.setCode(code.getCode());
        return m;
    }

    public static <T> Message<T> build(MessageCode code, T body) {
        Message<T> m = new Message<>();
        m.setDescription(code.getDescription());
        m.setCode(code.getCode());
        m.setBody(body);
        return m;
    }
}
