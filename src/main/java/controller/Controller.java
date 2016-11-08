package controller;

import client.Game;
import client.GameState;
import client.Question;
import communication.Connection;
import communication.MessageType;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.ResourceBundle;

public class Controller implements Initializable{

    public TextField TF_IP;
    public TextField TF_PORT;
    public TextField TF_NAME;
    public TextArea logTextArea;
    public TextField TF_MESSAGE;
    public GridPane questionGridPane;
    Connection connection;
    Game game;

    @FXML
    public void sendButtonAction(ActionEvent actionEvent){
        if(connection != null && TF_MESSAGE.getText() != null && !TF_MESSAGE.getText().equals("")) {
            connection.sendMessage(MessageType.ANSWERING_QUESTION_C, TF_MESSAGE.getText());
            TF_MESSAGE.clear();
        }
    }

    public void connectButtonAction(ActionEvent actionEvent) {
        if(connection == null) {
            String ip = TF_IP.getText();
            if (validIP(ip) && isPort(TF_PORT.getText())) {
                connection = new Connection(ip, Integer.parseInt(TF_PORT.getText()), this);
                if(connection.connect(game)){
                    clearQuestions();
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

        questionGridPane.setHgap(10);
        questionGridPane.setVgap(10);
        questionGridPane.getColumnConstraints().clear();

        for(int i = 0; i < 3; i++){
            ColumnConstraints col = new ColumnConstraints();
            col.setHalignment(HPos.CENTER);
            questionGridPane.getColumnConstraints().add(col);
        }

        TF_MESSAGE.setOnKeyPressed(new EventHandler<KeyEvent>()
        {
            @Override
            public void handle(KeyEvent ke)
            {
                if (ke.getCode().equals(KeyCode.ENTER))
                {
                    sendButtonAction(new ActionEvent());
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

        logTextArea.appendText(timeStamp + s + " \n");
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

    public void drawQuestions(ArrayList<Question> questions){
        clearQuestions();

        int count = 0;
        int maxRow = questions.size() / 3;
        questionGridPane.getRowConstraints().clear();

        for(int i = 0; i < maxRow; i++){
            RowConstraints row = new RowConstraints();
            row.setPercentHeight(20);
            row.setValignment(VPos.CENTER);
            questionGridPane.getRowConstraints().add(row);
        }

        for(int row = 0; row < maxRow; row++){
            for(int col = 0; col < 3; col++) {

                final Question question = questions.get(count++);
                Button button = new Button(question.category + "\n" + Integer.toString(question.points));
                button.setPrefWidth(120);
                button.setPrefHeight(90);
                button.setStyle("-fx-font-size: 13pt; ");
                button.setOnAction(new EventHandler<ActionEvent>() {
                    @Override public void handle(ActionEvent e) {
                        chooseQuestion(question.questionId);
                    }
                });
                button.setTextAlignment(TextAlignment.CENTER);

                if(!question.avaible){
                    button.setDisable(true);
                }

                questionGridPane.add(button, col, row);
            }
        }
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

    public void clearQuestions(){
        questionGridPane.getChildren().clear();
    }

    public void chooseQuestion(int questionId){
        if(connection != null && game.gameState == GameState.game_READY){
            connection.sendMessage(MessageType.CHOOSE_QUESTION_C, Integer.toString(questionId));
        }
    }


}
