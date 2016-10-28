package client;

import communication.Connection;
import communication.Message;
import controller.Controller;

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
                cont.gameMessage("msg: " + msg.data);
                break;
            }
            case READY_S:{
                READY_S(msg);
                break;
            }
            default:{
                cont.gameMessage("invalid message");
                break;
            }
        }
    }

}
