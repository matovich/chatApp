package src.chatApp;

import javafx.event.Event;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by tswed on 4/28/15.
 * Edited on 5/26/15.
 */
public class ChatClient implements Initializable {
    public TextField tfClientName;
    public TextArea taClientMessage;
    public TextArea taClientView;
    public Button btSend;
    private DataOutputStream toServer;
    private DataInputStream fromServer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            createClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createClient() throws IOException {
        // Create a socket to connect to the server
        Socket socket = new Socket("localhost", 8009);

        fromServer = new DataInputStream(socket.getInputStream());
        toServer = new DataOutputStream(socket.getOutputStream());

        new Thread(() -> {
            try {
                while (true) {
                    //Get info from server
                    String serverMessage = fromServer.readUTF();

                    // Display to the text area
                    taClientView.appendText(serverMessage + "\n");
                }
            } catch (IOException ex) {
                System.err.println(ex);
            }
        }).start();
    }

    public static void main(String[] args) {
    }

    public void handleSendButton(Event event) {
        try {
            // Get client name from the text field
            String name = tfClientName.getText();
            String message = name + ": " + taClientMessage.getText();

            taClientMessage.clear();

            // Send name to the server
            toServer.writeUTF(message);
            toServer.flush();
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }
}
