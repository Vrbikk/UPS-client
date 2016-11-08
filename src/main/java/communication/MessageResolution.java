package communication;

import client.Question;

import java.util.ArrayList;

/**
 * Created by vrbik on 8.10.16.
 */
public final class MessageResolution {

    private static final String DELIMETER = ":";
    private static final String ENDING = ";";


    public static final Message composeMessage(MessageType type, String data){
        String body = Integer.toString(type.ordinal()) + DELIMETER + data + ENDING;
        int len = body.getBytes().length;
        String raw = Integer.toString(len) + DELIMETER + body;
        return new Message(type, data, raw, len);
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

        int len_int;
        int type_int;

        try {
           len_int = Integer.parseInt(items[0]);
           type_int = Integer.parseInt(items[1]);
        }
        catch(Exception e) {
            return false;
        }

        if((len_int) != (items[1] + items[2]).getBytes().length + 2){  // 2 * DELIMETER,  without ;
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
                if(data.equals("ok")){ //FUG :--DDDDDDDDDD
                    return true;
                }else{
                    return false;
                }
            }
            case QUESTIONS_S:{
                try{
                    getQuestions(data);
                }catch(Exception e){
                    return false;
                }
                return true;
            }
            default:{
                return false;
            }
        }
    }

    public static final Message decomposeMessage(String input){
        String message = getMessage(input);
        String[] parts = message.split(DELIMETER);
        return new Message(MessageType.getMessageType(Integer.parseInt(parts[1])), parts[2], input, Integer.parseInt(parts[0]));
    }

    public static ArrayList<Question> getQuestions(String content) throws Exception {
        ArrayList<Question> questions = new ArrayList<Question>();

        String[] items = content.split("-");
        if(items.length < 6) {
            throw new Exception();
        }

        for(int i = 0; i < items.length; i++){
            String[] q = items[i].split("_");
            if(q.length != 4){
                throw new Exception();
            }

            int question_id = Integer.parseInt(q[0]);
            if(question_id < 0 || question_id > (items.length - 1)){
                throw new Exception();
            }

            String category = q[1];

            if(category == ""){
                throw new Exception();
            }

            int points = Integer.parseInt(q[2]);

            if(points < 0){
                throw new Exception();
            }

            boolean avaible;
            if(q[3].equals("1")){
                avaible = true;
            }else if(q[3].equals("0")){
                avaible = false;
            }else{
                throw new Exception();
            }

            Question question = new Question(question_id, points, avaible, category);
            questions.add(question);
        }
        return questions;
    }
}




