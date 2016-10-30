package communication;
import controller.Controller;
import javafx.application.Platform;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by vrbik on 8.10.16.
 */
public class TCPlistener extends Thread{

    private Connection con;
    public boolean running;
    private BufferedReader br;
    private Controller cont;


    public TCPlistener(Connection con, BufferedReader br, Controller cont){
        this.con = con;
        this.br = br;
        running = true;
        this.cont = cont;
    }

    @Override
    public void run() {
        String message;

        while(running){
            try {
                message = br.readLine();
                if(message != null){
                    if(MessageResolution.isValidMessage(message)) {
                        con.game.gameAction(MessageResolution.decomposeMessage(message));
                    }else{
                        System.out.println("bad MESSAGE recieved: " + message);
                    }
                }else{
                    throw new IOException();
                }
            } catch (IOException e) {

                try {
                    Thread.sleep(50); //FUCK JAVA why you so broken?
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }

                if(!con.userDisconnect){
                    Platform.runLater(new Runnable() {
                        @Override public void run() {
                            cont.MessageBox("Server disconnected!");
                        }});
                    try {
                        Thread.sleep(50); //FUCK JAVA why you so broken?
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    cont.disconnect();
                }
            }
        }
    }
}
