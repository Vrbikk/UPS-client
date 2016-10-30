package communication;

/**
 * Created by vrbik on 8.10.16.
 */
public enum MessageType {
    DEBUG, LOGIN_C, LOGIN_S, BROADCAST, UNICAST_S, READY_C, READY_S, QUESTIONS_S, CHOOSE_QUESTION_C, ERROR;

    public static boolean isMessageType(int index) {
        for(MessageType type : MessageType.values()) {
            if (type.ordinal() == index) return true;
        }
        return false;
    }

    public static MessageType getMessageType(int index){
        for(MessageType type : MessageType.values()){
            if(type.ordinal() == index) return type;
        }

        return null; // fucked up
    }
}
