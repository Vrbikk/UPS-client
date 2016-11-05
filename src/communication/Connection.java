package communication;

import client.Game;
import controller.Controller;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by vrbik on 7.10.16.
 */

public class Connection{

    private String ip;
    private int port;
    private Controller cont;
    private Socket socket;
    private PrintWriter outPrint;
    private BufferedReader inBuff;
    private TCPlistener tcpLISTENER;
    public Game game;
    public boolean userDisconnect = false;

    public Connection(String ip, int port, Controller cont) {
        this.ip = ip;
        this.port = port;
        this.cont = cont;
    }

    public boolean connect(Game game){
        try{
            this.game = game;
            socket = new Socket(ip, port);
            outPrint = new PrintWriter(socket.getOutputStream());
            inBuff = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            tcpLISTENER = new TCPlistener(this, inBuff, cont);
        }catch(Exception e){
            return false;
        }
        return true;
    }

    public void startListening(){
        tcpLISTENER.start();
    }


    public void disconnect(){
        try{
            tcpLISTENER.running = false;
            outPrint.close();
            inBuff.close();
            socket.close();
        }catch(Exception e){
            System.out.println("err");
        }
    }

    public void sendMessage(MessageType type, String data){
        Message newMessage = MessageResolution.composeMessage(type, data);
        synchronized(this){
            if(this.socket.isConnected()){
                outPrint.println(newMessage.raw);
                outPrint.flush();
            }else{
                System.out.println("SENDING FAILED");
            }
        }
    }
}
