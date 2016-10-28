package controller;

import client.Game;
import client.GameState;
import communication.Connection;
import communication.MessageType;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.ResourceBundle;

public class Controller implements Initializable{

    public TextField TF_IP;
    public TextField TF_PORT;
    public TextField TF_NAME;
    public TextArea logTextArea;
    public TextField TF_MESSAGE;
    public VBox questionHbox;

    Connection connection;
    Game game;

    @FXML
    public void sendButtonAction(ActionEvent actionEvent){
        connection.sendMessage(MessageType.DEBUG, TF_MESSAGE.getText());
    }

    public void connectButtonAction(ActionEvent actionEvent) {
        if(connection == null) {

            String ip = TF_IP.getText();

            if (validIP(ip) && isPort(TF_PORT.getText())) {
                connection = new Connection(ip, Integer.parseInt(TF_PORT.getText()), this);
                if(connection.connect(game)){
                    logTextArea.clear();
                    gameMessage("connected");
                    game.setUp(connection);
                    connection.startListening();
                }else{
                    MessageBox("Connection failed");
                    connection = null;
                }
            } else {
                MessageBox("Incorrect connection info, fix it and try again");
            }
        }else{
            MessageBox("Connection already created, you need to disconnect first!");
        }
    }

    public void readyButtonAction(ActionEvent actionEvent) {
        if(connection != null && game.gameState == GameState.game_LOGGED){
            connection.sendMessage(MessageType.READY_C, "ready");
        }else{
            MessageBox("could not perform ready");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        game = new Game(this);

        final int maxLenTF_NAME = 20;
        final int maxLenTF_MESSAGE = 50;

        TF_NAME.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
                if (TF_NAME.getText().length() > maxLenTF_NAME) {
                    String s = TF_NAME.getText().substring(0, maxLenTF_NAME);
                    TF_NAME.setText(s);
                }
            }
        });

        TF_MESSAGE.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
                if (TF_MESSAGE.getText().length() > maxLenTF_MESSAGE) {
                    String s = TF_MESSAGE.getText().substring(0, maxLenTF_MESSAGE);
                    TF_MESSAGE.setText(s);
                }
            }
        });
    }

    private boolean validIP (String ip) {
        try {
            if ( ip == null || ip.isEmpty() ) {
                return false;
            }

            String[] parts = ip.split( "\\." );
            if ( parts.length != 4 ) {
                return false;
            }

            for ( String s : parts ) {
                int i = Integer.parseInt( s );
                if ( (i < 0) || (i > 255) ) {
                    return false;
                }
            }
            if ( ip.endsWith(".") ) {
                return false;
            }

            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    private boolean isPort(String str)
    {
        int d;

        try
        {
            d = Integer.parseInt(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }

        if(d > 0 && d < 0xFFFF){
            return true;
        }

        return false;
    }

    public void MessageBox(String info){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText("Warning");
        alert.setResizable(true);
        alert.getDialogPane().setPrefSize(480, 200);
        alert.setContentText(info);
        alert.showAndWait();
    }

    public void gameMessage(String s){
        String timeStamp = new SimpleDateFormat("HH:mm:ss - ").format(Calendar.getInstance().getTime());

        s = s.replaceAll("/", "\n");

        logTextArea.appendText(timeStamp + s + "\n");
    }

    public void disconnectButtonAction(ActionEvent actionEvent) {
        if(connection != null) {
            connection.userDisconnect = true;
            disconnect();
        }
    }

    public void disconnect(){
            if (connection != null) {
                connection.disconnect();
                connection = null;
                gameMessage("disconnected");
            }
    }

    public void drawQuestions(){


    }

    public void loginButtonAction(ActionEvent actionEvent) {
        if(connection != null && game.gameState == GameState.game_LOGGING){
            if(TF_NAME.getText().replaceAll("\\s+", "").length() >= 2) {
                connection.sendMessage(MessageType.LOGIN_C, TF_NAME.getText());
            }else{
                gameMessage("LOGIN > 2 chars");
            }
        }else{
            MessageBox("you need to connect first!");
        }
    }
}