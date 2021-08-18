package open.zikun.rpc.api;

import java.io.Serializable;


/**
 * 被传递的消息实体
 */
public class HelloObject implements Serializable {
    // 消息id
    private int id;
    // 消息主体
    private String message;

    public HelloObject() {
    }

    public HelloObject(String message) {
        this.message = message;
    }

    public HelloObject(int id, String message) {
        this.id = id;
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
