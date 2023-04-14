package com.example.clienttemplateforcardsupdate2122;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javax.swing.JOptionPane;
import socketfx.Constants;
import socketfx.FxSocketClient;
import socketfx.SocketListener;

public class HelloController implements Initializable {
    boolean areReady = false, serverReady = false, drewCard = true, discarded = true;
    @FXML
    private Button sendButton, ready;
    @FXML
    private TextField sendTextField;
    @FXML
    private Button connectButton, disconnectButton, btnKnock, btnNextRound;
    @FXML
    private TextField portTextField;
    @FXML
    private TextField hostTextField;
    @FXML
    private Label lblName1, lblName2, lblName3, lblName4, lblMessages, connectedLabel, labelTurn, lblPoints;

    @FXML
    private GridPane gPaneServer, gPaneClient;


    private final static Logger LOGGER =
            Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
    private boolean isConnected, serverUNO = false, clientUNO = false;




    public enum ConnectionDisplayState {
        DISCONNECTED, WAITING, CONNECTED, AUTOCONNECTED, AUTOWAITING
    }
    private FxSocketClient socket;
    private void connect() {
        socket = new FxSocketClient(new FxSocketListener(),
                hostTextField.getText(),
                Integer.valueOf(portTextField.getText()),
                Constants.instance().DEBUG_NONE);
        socket.connect();
    }
    private void displayState(ConnectionDisplayState state) {
        switch (state) {
            case DISCONNECTED:
                connectButton.setDisable(false);
                disconnectButton.setDisable(true);
                sendButton.setDisable(true);
                sendTextField.setDisable(true);
                connectedLabel.setText("Not connected");
                break;
            case WAITING:
            case CONNECTED:
                connectButton.setDisable(true);
                disconnectButton.setDisable(false);
                sendButton.setDisable(false);
                sendTextField.setDisable(false);
                connectedLabel.setText("Connected");
                break;
            case AUTOCONNECTED:
                connectButton.setDisable(true);
                disconnectButton.setDisable(true);
                sendButton.setDisable(false);
                sendTextField.setDisable(false);
                connectedLabel.setText("Connected");
                break;
        }
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        isConnected = false;
        displayState(ConnectionDisplayState.DISCONNECTED);
        Runtime.getRuntime().addShutdownHook(new ShutDownThread());
    }

    class ShutDownThread extends Thread {
        @Override
        public void run() {
            if (socket != null) {
                if (socket.debugFlagIsSet(Constants.instance().DEBUG_STATUS)) {
                    LOGGER.info("ShutdownHook: Shutting down Server Socket");
                }
                socket.shutdown();
            }
        }
    }

    public HelloController(){
        try {
            back1 = new FileInputStream("src/main/resources/Images/BACK-1.jpg");
            imageBack = new Image(back1);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }
    class FxSocketListener implements SocketListener {

        @Override
        public void onMessage(String line) {
            lblMessages.setText(line);
            if (line.equals("ready") && areReady){
                ready.setVisible(false);
            } else if(line.equals("ready")){
                serverReady=true;
            }
            else if(line.equals("dealt")){
                imgDeck.setImage(imageBack);
            } else if(line.equals("sCardStart")){
                hand1D.clear();
            } else if(line.startsWith("sCards")){
                hand1D.add((new Card(line.substring(6))));
            } else if(line.equals("cCardStart")){
                hand2D.clear();
            }
            else if (line.startsWith("cCards")){
                hand2D.add(new Card(line.substring(6)));
            } else if(line.startsWith("sCardNum")){
                numInServerHand = Integer.parseInt(line.substring(8));
                for (ImageView x: hand1I){
                    x.setImage(null);
                }
                for (ImageView x: hand2I){
                    x.setImage(null);
                }
                for(int i=0;i<numInServerHand;i++){
                    hand1I.get(i).setImage(imageBack);
                }
                printCCards();
            }
            else if(line.equals("serverDrawCard")){
            }
            else if(line.equals("disPileStart")){
                discardPile.clear();
            } else if(line.startsWith("dPile")){
                discardPile.add(new Card(line.substring(5)));
            } else if(line.equals("printDiscard")){
                printDiscard();
            } else if(line.equals("serverEndTurn")){
                drewCard = false;
                labelTurn.setText("Your Turn");
            } else if(line.startsWith("order")){
                clientMeldSize = Integer.parseInt(line.substring(5));
                for(int i=0; i<clientMeldSize; i++){
                    hand2I.get(i).setOpacity(0.5);
                }
            } else if(line.equals("cCanKnock")){
                btnKnock.setDisable(false);
            } else if(line.startsWith("clientScore")){
                clientScore = Integer.parseInt(line.substring(11));
            } else if(line.startsWith("serverScore")){
                serverScore = Integer.parseInt(line.substring(11));
            } else if(line.equals("serverKnocked")){
                showServerHand();
            } else if(line.equals("serverFailedKnock")){
                printScore();
            } else if(line.equals("resetOpacity")){
                System.out.println("reset");
                for(ImageView image : hand2I){
                    image.setOpacity(1);
                }
            } else if(line.equals("serverRummy")){

            } else if(line.equals("clientRummy")){

            }
        }

        public void printCCards(){
            for (int i=0;i<hand2D.size();i++){
                try {
                    tempCard = new FileInputStream(hand2D.get(i).getCardPath());
                    imageFront = new Image(tempCard);
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                }
                hand2I.get(i).setImage(imageFront);
            }
        }
        @Override
        public void onClosedStatus(boolean isClosed) {
            if (isClosed) {
                isConnected = false;
                displayState(ConnectionDisplayState.DISCONNECTED);
            } else {
                isConnected = true;
                displayState(ConnectionDisplayState.CONNECTED);
            }
        }
    }
    @FXML
    private void handleReady(ActionEvent event) {
        areReady=true;
        socket.sendMessage("ready");
        if (serverReady){
            ready.setVisible(false);
        }else{
            ready.setDisable(true);
        }
        hand1I.add(imgS0);
        hand1I.add(imgS1);
        hand1I.add(imgS2);
        hand1I.add(imgS3);
        hand1I.add(imgS4);
        hand1I.add(imgS5);
        hand1I.add(imgS6);
        hand1I.add(imgS7);
        hand1I.add(imgS8);
        hand1I.add(imgS9);
        hand1I.add(imgS10);

        hand2I.add(imgC0);
        hand2I.add(imgC1);
        hand2I.add(imgC2);
        hand2I.add(imgC3);
        hand2I.add(imgC4);
        hand2I.add(imgC5);
        hand2I.add(imgC6);
        hand2I.add(imgC7);
        hand2I.add(imgC8);
        hand2I.add(imgC9);
        hand2I.add(imgC10);
    }

    @FXML
    public void handleDrawFromDeck(){
        if (!drewCard){
            drewCard = true;
            discarded = false;
            socket.sendMessage("clientDrawFromDeck");
        }
    }
    @FXML
    public void handleDrawFromDiscard(){
        if (!drewCard){
            drewCard = true;
            discarded = false;

            socket.sendMessage("clientDrawFromDiscard");
        }
    }

    @FXML
    public void handleKnock(){
        socket.sendMessage("cKnocked");
        System.out.println(serverScore + ", " + clientScore);
        btnNextRound.setDisable(false);

    }


    public void printDiscard(){
        if(discardPile.size() <= 0){
            imgDiscard.setImage(null);
        } else {
            try{
                tempCard = new FileInputStream(discardPile.get(discardPile.size()-1).getCardPath());
                imageFront = new Image(tempCard);
            } catch (FileNotFoundException e){
                e.printStackTrace();
            }
            imgDiscard.setImage(imageFront);
        }
    }
    public void handleClientImgClicked(MouseEvent mouseEvent) {
        if(drewCard && !discarded){
            discarded = true;
            imgClicked = GridPane.getColumnIndex((ImageView) mouseEvent.getSource());
            socket.sendMessage("cic"+ imgClicked);
            socket.sendMessage("clientEndTurn");
            labelTurn.setText("Opponent's Turn");
        }
    }

    public void showServerHand(){
        for(ImageView image : hand1I){
            image.setImage(null);
        }
        for(int i=0; i<hand1D.size(); i++){
            try{
                tempCard = new FileInputStream(hand1D.get(i).getCardPath());
                imageFront = new Image(tempCard);
            } catch(FileNotFoundException e){
                e.printStackTrace();
            }
            hand1I.get(i).setImage(imageFront);
        }
    }

    public void printScore(){
        int tempScore = clientScore - serverScore;
        if(tempScore >= 0){
            totalScore += tempScore;
        }

        lblPoints.setText("" + totalScore);
    }

    @FXML
    public void handleNextRound(){

    }

    @FXML
    private void handleConnectButton(ActionEvent event) {
        displayState(ConnectionDisplayState.WAITING);
        connect();
    }
    @FXML
    public void handleDisconnect(){
        displayState(ConnectionDisplayState.DISCONNECTED);
        socket.shutdown();
    }
    @FXML
    private ImageView imgS0,imgS1,imgS2,imgS3,imgS4,imgS5,imgS6,imgS7,imgS8,imgS9, imgS10,
            imgC0,imgC1,imgC2,imgC3,imgC4, imgC5,imgC6,imgC7,imgC8,imgC9, imgC10,imgDiscard, imgDeck;
    FileInputStream back1,tempCard;
    Image imageBack;
    Image imageFront;
    List<ImageView> hand1I = new ArrayList<>();
    List<ImageView> hand2I = new ArrayList<>();
    List<Card> hand1D = new ArrayList<>();
    List<Card> hand2D = new ArrayList<>();
    List<Card> discardPile = new ArrayList<>();
    int numInServerHand=0;
    int imgClicked;
    int clientMeldSize;
    int serverScore;
    int clientScore;
    int totalScore = 0;


}