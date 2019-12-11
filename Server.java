package sample;

/****************************************************************************************
 Filename: Server.java
 Author: Andressa Pessoa de Araujo Machado [040923007]
 Course: CST8221 - Java Applications, Lab Section 302
 Assignment Number:Assignment#2, Part2
 Due Date: 2019/12/06
 Submission 2019/12/06
 Professor's Name: Daniel Cormier.
 Purpose: Class that controls the serverSocket and waits for connections of clients
 ***************************************************************************************/

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) {
        int port = 65535;
        int friend = 0;

        if (args.length > 1) {
            port = Integer.parseInt(args[1]);
            System.out.println("Using port: " + port);
        } else {
            System.out.println("Using default port: " + port);
        }

        ServerSocket server = null;

        try {
            server = new ServerSocket(port);

            Platform.setImplicitExit(false);

            while (true) {
                Socket socket = server.accept();

                if (socket.getSoLinger() != -1) {
                    socket.setSoLinger(true, 5);
                }
                if (!socket.getTcpNoDelay()) {
                    socket.setTcpNoDelay(true);
                }

                System.out.println("Connecting to a client " + socket.toString());

                friend++;

                final String title = "Andressa's Friend " + friend;
                launchClient(socket, title);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private static void launchClient(Socket in, String title) {
        //  new ServerChatUI(in, title).start(new Stage());
        try {
            new JFXPanel();
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    new ServerChatUI(in, title).start(new Stage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
