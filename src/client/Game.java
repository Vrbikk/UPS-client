package client;

import communication.Connection;
import communication.Message;
import communication.MessageResolution;
import controller.Controller;
import javafx.application.Platform;
import java.util.ArrayList;

class DrawTask implements Runnable {
    private ArrayList<Question> questions;
    private Controller cont;
    DrawTask(ArrayList<Question> questions, Controller controller) {
        this.questions = questions;
        this.cont = controller;
    }

    public void run() {
        cont.drawQuestions(questions);
    }
}

/**
 * Created by vrbik on 7.10.16.
 */
public class Game {

    private Connection connection;
    private Controller cont;

    private int id;

    public GameState gameState;

    public Game(Controller cont) {
        gameState = GameState.game_LOGGING;
        this.cont = cont;
    }

    public void setUp(Connection connection){
        this.gameState = GameState.game_LOGGING;
        this.connection = connection;
    }

    public void LOGIN_S(Message msg){
        if(gameState == gameState.game_LOGGING){
                id = Integer.parseInt(msg.data);
                cont.gameMessage("Logged as (" + cont.TF_NAME.getText() + ") [" + id + "]");
                gameState = GameState.game_LOGGED;
        }else{
            cont.gameMessage("recieving dupl. login answer");
        }
    }

    public void READY_S(Message msg){
        if(msg.data.equals("ok")){
            gameState = GameState.game_READY;
        }
    }

    public void QUESTIONS_S(Message msg){
        try {
            Platform.runLater(new DrawTask(MessageResolution.getQuestions(msg.data), cont));
        } catch (Exception e) {
            System.out.println("JAVAFX ERROR");
        }
    }

    synchronized public void gameAction(Message msg){

        System.out.println("input: " + msg);

        switch(msg.type){
            case DEBUG:{
                break;
            }
            case LOGIN_S:{
                LOGIN_S(msg);
                break;
            }
            case BROADCAST:{
                cont.gameMessage(msg.data);
                break;
            }
            case ERROR:{
                cont.gameMessage("error: " + msg.data);
                break;
            }
            case UNICAST_S:{

                System.out.println("unicast_S");

                cont.gameMessage("msg: " + msg.data);
                break;
            }
            case READY_S:{
                READY_S(msg);
                break;
            }
            case QUESTIONS_S:{
                QUESTIONS_S(msg);
                break;
            }
            default:{
                cont.gameMessage("invalid message");
                break;
            }
        }
    }

}
