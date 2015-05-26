package src.chatApp;

import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

public class ChatServer implements Initializable{
    public TextArea taServerWindow;
    private int clientNo = 0;
    private ArrayList<DataOutputStream> clients = new ArrayList<>();

    public void initialize(URL location, ResourceBundle resources) {
        createThread();
    }

    private void createThread() {
        new Thread( () -> {
            try {
                ServerSocket serverSocket = new ServerSocket(8009);
                taServerWindow.appendText("MultiThreadServer started at " + new Date() + "\n");

                while (true) {
                    Socket clientSocket = serverSocket.accept();

                    clientNo++;

                    Platform.runLater(() -> {
                        taServerWindow.appendText("Starting thread for client " + clientNo + " at " + new Date() + "\n");

                        InetAddress inetAddress = clientSocket.getInetAddress();
                        taServerWindow.appendText("Client " + clientNo + "'s host name is " + inetAddress.getHostName() + "\n");
                        taServerWindow.appendText("Client " + clientNo + "'s IP Address is " + inetAddress.getHostAddress() + "\n");
                    });

                    new Thread(new HandleClients(clientSocket)).start();

                }
            }
            catch(IOException ex) {
                System.err.println(ex);
            }
        }).start();
    }

    class HandleClients implements Runnable {
        private Socket socket;

        public HandleClients(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                DataInputStream inputFromClient = new DataInputStream(socket.getInputStream());
                DataOutputStream outputToClient = new DataOutputStream(socket.getOutputStream());

                clients.add(outputToClient);

                while (true) {
                    String clientMessage = inputFromClient.readUTF();

                    //String shouldExit = "";

//                while (shouldExit != "Exit") {
//                    String clientMessage = inputFromClient.readUTF();
//
//                    if (clientMessage != null && !clientMessage.isEmpty()) {
//                        if (clientMessage.toUpperCase().endsWith("EXIT")) {
//                            shouldExit = "EXIT";
//                        }
//
//                        MessageDatabase.appendMessage(clientMessage + "\n");
//                    }

                    for (DataOutputStream client : clients) {
                        client.writeUTF(clientMessage);
                    }
                }
            }
            catch(IOException e) {
                System.out.println("I am handling this error");
                e.printStackTrace();
            }
        }
    }
}
