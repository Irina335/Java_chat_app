package com.example.socket_chat_client;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private AnchorPane ap_main;

    @FXML
    private Button button_send;

    @FXML
    private ScrollPane sp_main;

    @FXML
    private VBox vbox_messages;

    @FXML
    private TextField tf_message;
    private  Client client;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try{
            //connection au serveur avec le meme nom de la hote et le meme port
            client = new Client(new Socket("localhost",1234));
            System.out.println("Connecté au serveur");
        }catch (IOException e){
            e.printStackTrace();
            System.out.println("Erreur lors de la connection au serveur");
        }
        //on va rajouter de l'event onchange , car on veut que le msg envoyé va scroller en haut à l'aide du scrollpane (coté gui)
        vbox_messages.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                // modification de la valeur du scrollpane
                sp_main.setVvalue((Double) t1);
            }
        });
        //recoit le message venant du serveur dans un thread séparé
        client.receiveMessageFromServer(vbox_messages);

        button_send.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                String messageToSend = tf_message.getText();
                if (!messageToSend.isEmpty()){
                    //si le message n'est pas vide on va creer dynamiquement le zone de texte ainsi que son conteneur (coté gui)
                    HBox hBox = new HBox();
                    hBox.setAlignment(Pos.CENTER_RIGHT);
                    hBox.setPadding(new Insets(5,5,5,10));

                    Text text = new Text(messageToSend);
                    TextFlow textFlow = new TextFlow(text);

                    textFlow.setStyle("-fx-color: rgb(239,242,255); " +
                            "-fx-background-color: rgb(15,125,242); " +
                            "-fx-background-radius: 20px");

                    textFlow.setPadding(new Insets(5,10,5,10));
                    text.setFill(Color.color(0.934,0.945,0.996));

                    hBox.getChildren().add(textFlow);
                    vbox_messages.getChildren().add(hBox);

                    //ENvoie le message au serveur
                    client.sendMessageToServer(messageToSend);
                    tf_message.clear();
            }}
        });

    }
    public static  void addLabel(String msgFromServer,VBox vBox){
        HBox hBox = new HBox();
        hBox.setAlignment((Pos.CENTER_LEFT));
        hBox.setPadding(new Insets(5,5,5,10));

        Text text = new Text(msgFromServer);
        TextFlow textFlow = new TextFlow(text);
        textFlow.setStyle("-fx-background-color: rgb(233,233,235) " +
                "-fx-background-radius: 20px");
        textFlow.setPadding(new Insets(5,10,5,10));
        hBox.getChildren().add(textFlow);

        //nous allons creer un runnable() , pour executer ce methode à l'aide d'un thread
        //que l'on va creer dans la classe Client
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                vBox.getChildren().add(hBox);
            }
        });
    }


}