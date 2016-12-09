package communication;

import client.Game;
import controller.Controller;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by vrbik on 7.10.16.
 */

public class Connection{

    private String ip;
    private int port;
    private Controller cont;
    public Socket socket;
    private PrintWriter outPrint;
    private BufferedReader inBuff;
    private TCPlistener tcpLISTENER;
    public Game game;
    public boolean userDisconnect = false;
    public Timer timer;

    public Connection(String ip, int port, Controller cont) {
        this.ip = ip;
        this.port = port;
        this.cont = cont;
    }

    public boolean connect(Game game){

        try{
            this.game = game;
            Thread socketThread=new Thread() {
                public void run() {
                    try {
                        socket = new Socket(ip, port);
                    }
                    catch (Exception e) {
                        // don't care here
                    }
                }
            };
            socketThread.start();

            //socket = new Socket(ip, port);

            socketThread.join(1200);
            socket.setSoTimeout(3000);

            outPrint = new PrintWriter(socket.getOutputStream());
            inBuff = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            tcpLISTENER = new TCPlistener(this, inBuff, cont);


            timer = new Timer(true);
            timer.scheduleAtFixedRate(
                    new TimerTask() {
                        public void run() { sendKeepAlivePacket(); }
                    }, 0, 1000);


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
            timer.cancel();
            timer.purge();
            timer = null;
            tcpLISTENER.running = false;
            outPrint.close();
            inBuff.close();
            socket.close();
        }catch(Exception e){
            System.out.println("err");
        }
    }

    public void sendKeepAlivePacket(){
        sendMessage(MessageType.DEBUG, "are you there");
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
