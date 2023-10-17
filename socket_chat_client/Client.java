package com.example.socket_chat_client;

import javafx.scene.layout.VBox;

import java.io.*;
import java.net.Socket;

public class Client {

    //pour la communication avec le serveur
    private Socket socket;
    //pour lire tout ce que le serveur a envoyé.
    private BufferedReader bufferedReader;
    // pour écrire au server
    private BufferedWriter  bufferedWriter;

    //On a créer un constructeur parametré de la classe Client ,
    // (que l'on va utiliser dans le controllor pour rajouter un port et permettre la connection
    public Client(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            closeEverything(socket,bufferedReader,bufferedWriter);
        }
    }
    public  void sendMessageToServer(String messageToServer){
        //on utilise buffer pour une performance  rapide
        //bufferwritter est utilisée pour fournir une mise en mémoire tampon pour les instances de Writer.
        try{
            bufferedWriter.write(messageToServer);
            //newLine() est utilisé pour ajouter une nouvelle ligne en écrivant un séparateur de ligne.
            bufferedWriter.newLine();
            // flush()  est utilisé pour vider le flux d'entrée.
            bufferedWriter.flush();
        }catch(IOException e){
            e.printStackTrace();
            System.out.println("Erreur de l'envoie de message au server");
            closeEverything(socket,bufferedReader,bufferedWriter);
        }
    }

    public void receiveMessageFromServer(VBox vBox) {
        //on a crée un thread car on veut pas que tout le programme attend
        //juste pour recevoir un message  , donc on peut faire autre chose jusqu'à ce que l'on recoit un message
        new Thread(new Runnable() {
            @Override
            public void run() {
                //tant que le socket est connecté le client peut lire les msg from server
                while (socket.isConnected()) {
                    try {
                        //Lit le texte à partir d'un flux d'entrée de caractères,
                        // en mettant les caractères en mémoire tampon afin de permettre une lecture efficace des caractères
                        String messageFromServer= bufferedReader.readLine();
                        //ajout du message dans le conteneur vBox
                        MainController.addLabel(messageFromServer, vBox);

                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("Erreur de la reception du message");
                        closeEverything(socket,bufferedReader,bufferedWriter);
                        break;
                    }
                }
            }
        }).start();
    }


   //Ferme tous
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        try{
            if (bufferedReader != null){
                bufferedReader.close();
            }
            if (bufferedWriter != null){
                bufferedWriter.close();
            }
            if (socket !=null){
                socket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }

    }
}
