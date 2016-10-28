package communication;

/**
 * Created by vrbik on 8.10.16.
 */
public class Message {
    public String raw;
    public MessageType type;
    public String data;
    public int length;

    public Message(MessageType type, int length, String data, String raw) {
        this.type = type;
        this.data = data;
        this.raw = raw;
        this.length = length;
    }

    @Override
    public String toString() {
        return "Message{" +
                "raw='" + raw + '\'' +
                ", type=" + type +
                ", data='" + data + '\'' +
                ", length=" + length +
                '}';
    }
}
