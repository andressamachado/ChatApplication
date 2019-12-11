package sample;

/****************************************************************************************
 Filename: ChatRunnable.java
 Author: Andressa Pessoa de Araujo Machado [040923007]
 Course: CST8221 - Java Applications, Lab Section 302
 Assignment Number:Assignment#2, Part2
 Due Date: 2019/12/06
 Submission 2019/12/06
 Professor's Name: Daniel Cormier.
 Purpose: Class that implements the Runnable interface which controls the receiving messages
 on each part of the application (server and client).
 ***************************************************************************************/

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Class that implements the Runnable interface which controls the receiving messages
 * on each part of the application (server and client).
 *
 * @author Andressa Machado
 * @version 1.0
 * @see javafx.application.Application;
 * @since jdk1.8.0_221
 */
public class ChatRunnable<T extends Application & Accessible> implements Runnable {
    /**
     * {@value} Application UI screens (Client and Server)
     */
    private final T ui;
    /**
     * {@value} Reference to one endpoint of the two threat running on the network
     */
    private final Socket socket;
    /**
     * {@value} Reference to the connection for receiving messages
     */
    private ObjectInputStream inputStream;
    /**
     * {@value} Reference to the connection for sending messages
     */
    private ObjectOutputStream outputStream;
    /**
     * {@value} Reference to the area where the communication will be displayed
     */
    private final TextArea display;

    /**
     * Constructor. Initializes the variables.
     *
     * @param ui         - Reference to the interface that the runnable is controlling
     * @param connection - is a wrapper to control the socket and the input and output streams
     */
    public ChatRunnable(T ui, ConnectionWrapper connection) {
        this.ui = ui;
        this.display = ui.getDisplay();
        this.socket = connection.getSocket();
        this.inputStream = connection.getInputStream();
        this.outputStream = connection.getOutputStream();
    }

    /**
     * Overwritten method main method of the Runnable interface.
     */
    @Override
    public void run() {
        String strin;

        while (true) {
            if (socket.isClosed()) {
                break;
            }

            try {
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("MMMM dd, HH:mm a");
                strin = (String) inputStream.readObject();
                strin = strin.trim();

                if (strin.equals(ChatProtocolConstants.CHAT_TERMINATOR)) {
                    final String terminate = ChatProtocolConstants.DISPLACEMENT + timeFormatter.format(LocalDateTime.now()) +
                            ChatProtocolConstants.LINE_TERMINATOR + strin;
                    Platform.runLater(() -> display.appendText("\n" + terminate));
                    break;
                }

                final String append = ChatProtocolConstants.DISPLACEMENT + timeFormatter.format(LocalDateTime.now()) + ": \n" + ChatProtocolConstants.DISPLACEMENT + strin + "\n";
                Platform.runLater(() -> display.appendText(append));

            } catch (IOException | ClassNotFoundException | IllegalArgumentException exception) {
                exception.printStackTrace();
                break;
            }
        }

        if (!socket.isClosed()) {
            try {
                outputStream.writeObject(ChatProtocolConstants.DISPLACEMENT + ChatProtocolConstants.CHAT_TERMINATOR +
                        ChatProtocolConstants.LINE_TERMINATOR);
                outputStream.flush();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        ui.closeChat();
    }
}
