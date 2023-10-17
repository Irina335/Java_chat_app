package com.example.socket_chat;

import javafx.scene.layout.VBox;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private ServerSocket serverSocket;
    private Socket socket;
    //pour lire l'info que le client a envoyé (obtenu grace au socket)
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    //On a créer un constructeur parametré de la classe Server ,
    // (que l'on va utiliser dans le controllor pour rajouter un port et permettre la connection
    public Server(ServerSocket serverSocket) {
       try {
           this.serverSocket = serverSocket;
           //on utilise ce methode accept pour retourner l'objet socket lors des demandes entrantes au socket;
           //tant qu'un client n'est pas connecte le programme va toujours envoyer l'obj socket
           // donc quand le server recoit un socket venant du client il va creer un conection pour permettre la communication
           //Pour terminer la demande, le gestionnaire de sécurité vérifie l'adresse de l'hôte, le numéro de port (voir MainController ligne 50)
           this.socket = serverSocket.accept();
           this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
           this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
       }catch ( IOException e){
           e.printStackTrace();
           closeEverything(socket,bufferedReader,bufferedWriter);
       }
    }

    public void sendMessageToClient(String messageToClient){
        //on utilise buffer pour une performance  rapide
        //bufferwritter est utilisée pour fournir une mise en mémoire tampon pour les instances de Writer.
        try{
            bufferedWriter.write(messageToClient);
            //newLine() est utilisé pour ajouter une nouvelle ligne en écrivant un séparateur de ligne.
            bufferedWriter.newLine();
            // flush()  est utilisé pour vider le flux d'entrée.
            bufferedWriter.flush();
        }catch(IOException e){
            e.printStackTrace();
            System.out.println("Erreur lors de l'envoi du msg");
            closeEverything(socket,bufferedReader,bufferedWriter);
        }

    }

    public void receiveMessageFromClient(VBox vBox) {
        //on a crée un thread car on veut pas que tout le programme attend
        //juste pour recevoir un message  , donc on peut faire autre chose jusqu'à ce que l'on recoit un message
        new Thread(new Runnable() {
            @Override
            public void run() {
                //tant que le socket est connecté , le serveur peut recevoir le message from client
                while (socket.isConnected()) {
                    try {
                        // bufferedReader => Lit le texte à partir d'un flux d'entrée de caractères,
                        // en mettant les caractères en mémoire tampon afin de permettre une lecture efficace des caractères
                        String messageFromClient = bufferedReader.readLine();
                        MainController.addLabel(messageFromClient, vBox);

                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("Error receiving msg");
                        closeEverything(socket,bufferedReader,bufferedWriter);
                        break;
                    }
                }
            }
        }).start();
    }




    //ferme tous les sockets et les buffered
    public void closeEverything(Socket socket,BufferedReader bufferedReader,BufferedWriter bufferedWriter){
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
