package communication;

/**
 * Created by vrbik on 8.10.16.
 */
public class Message {
    public String raw;
    public MessageType type;
    public String data;
    int len;

    public Message(MessageType type, String data, String raw, int len) {
        this.type = type;
        this.data = data;
        this.raw = raw;
        this.len = len;
    }

    @Override
    public String toString() {
        return "Message{" +
                "raw='" + raw + '\'' +
                ", type=" + type +
                ", data='" + data + '\'' +
                '}';
    }
}
