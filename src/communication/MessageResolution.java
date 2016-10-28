package communication;

/**
 * Created by vrbik on 8.10.16.
 */
public final class MessageResolution {

    private static final String DELIMETER = ":";
    private static final String ENDING = ";";


    public static final Message composeMessage(MessageType type, String data){
        String body = DELIMETER + Integer.toString(type.ordinal()) + DELIMETER + data + ENDING;
        return new Message(type, body.length(), data, Integer.toString(body.length()) + body);
    }

    private static String getMessage(String input){
        String[] arr = input.split(ENDING);
        return arr[0];
    }

    public static final boolean isValidMessage(String input){

        if(input == null || input.equals("")) return false;

        String message = getMessage(input);

        if(message.length() < 5) return false;

        String[] items = message.split(DELIMETER);

        if(items.length != 3) return false;

        int type_int;
        int len_int;

        try {
           len_int = Integer.parseInt(items[0]);
           type_int = Integer.parseInt(items[1]);
        }
        catch(Exception e) {
            return false;
        }

        if((len_int - 1) != (items[1].length() + items[2].length() + 2)) {// 2 * DELIMETER,  without ;
            return false;
        }

        if(!MessageType.isMessageType(type_int)){
            return false;
        }

        if(!advancedDataValidation(items[2], MessageType.getMessageType(type_int))){
            return false;
        }

        return true;
    }

    public static boolean advancedDataValidation(String data, MessageType type){
        switch(type){
            case DEBUG:{
                return true;
            }
            case LOGIN_S:{
                int id;
                try {
                    id = Integer.parseInt(data);
                }
                catch(Exception e) {
                    return false;
                }

                if(id >= 0){
                    return true;
                }else{
                    return false;
                }
            }
            case BROADCAST:{
                return true;
            }
            case ERROR:{
                return true;
            }
            case UNICAST_S:{
                return true;
            }
            case READY_S:{
                if(data.equals("ok")){
                    return true;
                }else{
                    return false;
                }
            }
            default:{
                return false;
            }
        }
    }

    public static final Message decomposeMessage(String input){
        String message = getMessage(input);
        String[] parts = message.split(DELIMETER);
        return new Message(MessageType.getMessageType(Integer.parseInt(parts[1])), Integer.parseInt(parts[0]), parts[2], input);
    }

}
