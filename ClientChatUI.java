package sample;

/****************************************************************************************
 Filename:ClientChatUI.java
 Author: Andressa Pessoa de Araujo Machado [040923007]
 Course: CST8221 - Java Applications, Lab Section 302
 Assignment Number:Assignment#2, Part2
 Due Date: 2019/12/06
 Submission 2019/12/06
 Professor's Name: Daniel Cormier.
 Purpose: Class containing every method necessary to create the Client User Interface and
 controls the connection with the server
 ***************************************************************************************/

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;


/**
 * Class containing every method necessary to create the Client User Interface and
 * controls the connection with the server
 *
 * @author Andressa Machado
 * @version 1.0
 * @since jdk1.8.0_221
 */
public class ClientChatUI extends Application implements Accessible {
    /**
     * {@value} Reference to the message box where the user will write the message
     */
    private TextField message;
    /**
     * {@value} Reference to the send button
     */
    private Button sendButton;
    /**
     * {@value} Reference to the area where the dialog  will be displayed
     */
    private TextArea display;
    /**
     * {@value} Reference to the connection for sending messages
     */
    private ObjectOutputStream outputStream;
    /**
     * {@value} Reference to one endpoint of the two threat running on the network
     */
    private Socket socket;
    /**
     * {@value} Instance of ConnectionWrapper. Controls the socket and the input and output streams
     */
    private ConnectionWrapper connection;
    /**
     * {@value} Reference to the connection button
     */
    private Button connectButton;
    /**
     * {@value} Reference to the text field where the host to be connected will be entered
     */
    private TextField localHost;
    /**
     * {@value} Reference to the combo box holding the previously entered port numbers
     */
    private ComboBox<String> comboBoxPorts;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // stage > scene > container > node
        try {
            primaryStage.setTitle("Andressa`s ClientChatUI");
            primaryStage.setWidth(588);
            primaryStage.setHeight(500);
            primaryStage.setResizable(false);
            primaryStage.setX(100);
            primaryStage.setY(100);
            Scene scene = createScene();
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Scene createScene() {
        VBox box = new VBox();
        box.setPadding(new Insets(5, 5, 5, 5));
        Controller handler = new Controller();

        //CONNECTION PANE
        Label host = new Label("_Host: ");
        host.setMnemonicParsing(true);
        localHost = new TextField("localhost");
        host.setLabelFor(localHost);
        localHost.setEditable(true);

        Label port = new Label("_Port: ");
        port.setMnemonicParsing(true);

        String[] portNumbers = {" ", "8089", "65000", "65535"};
        comboBoxPorts = new ComboBox<>(FXCollections.observableArrayList(portNumbers));
        comboBoxPorts.setEditable(true);
        comboBoxPorts.setPrefWidth(128);
        port.setLabelFor(comboBoxPorts);
        //comboBoxPorts.setOnAction(handler);

        TilePane tilePane = new TilePane(comboBoxPorts);

        connectButton = new Button("_Connect");
        connectButton.setPrefWidth(128);
        connectButton.setMnemonicParsing(true);
        connectButton.setStyle("-fx-background-color: red");
        connectButton.setOnAction(handler);

        GridPane gridPane = new GridPane();
        gridPane.add(host, 0, 0);
        gridPane.add(localHost, 1, 0, 2, 1);
        gridPane.add(port, 0, 1);
        gridPane.add(comboBoxPorts, 1, 1);
        gridPane.add(connectButton, 2, 1);
        // gridPane.setBackground(new Background(new BackgroundFill(Color.rgb(242, 242, 242), CornerRadii.EMPTY, Insets.EMPTY)));
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(5, 5, 5, 5));

        ColumnConstraints constraints1 = new ColumnConstraints();
        ColumnConstraints constraints2 = new ColumnConstraints();
        ColumnConstraints constraints3 = new ColumnConstraints();
        constraints1.setPercentWidth(6);
        constraints2.setPercentWidth(25);
        constraints3.setPercentWidth(69);
        gridPane.getColumnConstraints().addAll(constraints1, constraints2, constraints3);

        BorderedTitledPane connectionPane = new BorderedTitledPane("CONNECTION", Pos.TOP_LEFT, Color.rgb(255, 0, 0), gridPane);

        //MESSAGE PANE
        message = new TextField();
        message.setPromptText("Type a message");
        localHost.setEditable(true);

        sendButton = new Button("_Send");
        sendButton.setPrefWidth(131);
        sendButton.setDisable(true);
        sendButton.setMnemonicParsing(true);
        sendButton.setOnAction(handler);

        GridPane gridPane1 = new GridPane();
        gridPane1.add(message, 0, 0);
        gridPane1.add(sendButton, 1, 0);

        gridPane1.setBackground(new Background(new BackgroundFill(Color.rgb(242, 242, 242), CornerRadii.EMPTY, Insets.EMPTY)));
        gridPane1.setVgap(10);
        gridPane1.setPadding(new Insets(5, 5, 5, 5));

        ColumnConstraints constraints4 = new ColumnConstraints();
        ColumnConstraints constraints5 = new ColumnConstraints();
        constraints4.setPercentWidth(75);
        constraints5.setPercentWidth(25);
        gridPane1.getColumnConstraints().addAll(constraints4, constraints5);
        gridPane1.setHgap(5);

        BorderedTitledPane messagePane = new BorderedTitledPane("MESSAGE", Pos.TOP_LEFT, Color.rgb(0, 0, 0), gridPane1);

        //CHAT DISPLAY PANE:
        display = new TextArea();
        display.setEditable(false);
        display.setMinHeight(244);
        // display.setBackground(new Background(new BackgroundFill(Color.rgb(242, 242, 242), CornerRadii.EMPTY, Insets.EMPTY)));
        //display.setPadding(new Insets(5, 5, 5, 5));

        BorderedTitledPane displayPane = new BorderedTitledPane("CHAT DISPLAY", Pos.TOP_CENTER, Color.rgb(0, 0, 255), display);

        box.getChildren().add(connectionPane);
        box.getChildren().add(messagePane);
        box.getChildren().add(displayPane);
        box.setSpacing(7);

        Scene scene = new Scene(box, 300, 300);

        return scene;
    }

    @Override
    public TextArea getDisplay() {
        return display;
    }

    @Override
    public void closeChat() {
        if (!socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            } finally {
                enableConnectionButton();
                sendButton.setDisable(true);
                display.appendText("\n");
            }
        }
    }

    private void enableConnectionButton() {
        connectButton.setDisable(false);
        connectButton.setStyle("-fx-background-color: red");
        sendButton.setDisable(true);
    }

    public void stop() {

        if (socket != null && !socket.isClosed()) {
            try {
                outputStream.writeObject(ChatProtocolConstants.CHAT_TERMINATOR);
                outputStream.flush();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        Platform.exit();
    }

    private class BorderedTitledPane extends StackPane {

        public BorderedTitledPane(String titleName, Pos titlePosition, Color borderColor, Node node) {
            this.setPadding(new Insets(10, 10, 10, 10));
            this.setBackground(new Background(new BackgroundFill(borderColor, CornerRadii.EMPTY, Insets.EMPTY)));

            Label title = new Label(titleName);
            title.setFont(new Font("Calibri bold", 12));
            title.setPadding(new Insets(5, 10, 5, 10));
            title.setTranslateX(0);
            title.setBackground(new Background(new BackgroundFill(Color.rgb(242, 242, 242), CornerRadii.EMPTY, (new Insets(5, 5, 5, 5)))));
            StackPane.setAlignment(title, titlePosition);
            //elevate the title to stay in the same level of the border.
            StackPane.setMargin(title, new Insets(-15, 0, 0, 0));

            StackPane holder = new StackPane();
            holder.setBorder(new Border(new BorderStroke(Color.rgb(242, 242, 242), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
            holder.setPadding(new Insets(5, 5, 5, 5));
            holder.getChildren().add(node);
            holder.setBackground(new Background(new BackgroundFill(Color.rgb(242, 242, 242), CornerRadii.EMPTY, Insets.EMPTY)));

            this.getChildren().addAll(holder, title);
        }
    }

    private class Controller implements EventHandler<ActionEvent> {
        boolean isConnected = false;

        @Override
        public void handle(ActionEvent event) {
            String host = null;

            if (event.getTarget().toString().contains("_Connect")) {
                host = localHost.getText();

                int port = Integer.parseInt(comboBoxPorts.getValue());

                isConnected = connect(host, port);

                if (isConnected) {
                    connectButton.setDisable(true);
                    connectButton.setStyle("-fx-background-color: blue");

                    sendButton.setDisable(false);
                    display.requestFocus();

                    Runnable runnable = new ChatRunnable<>(ClientChatUI.this, connection);
                    Thread thread = new Thread(runnable);
                    thread.start();
                }
            }

            if (!isConnected) {
                return;
            }

            if (event.getTarget().toString().contains("_Send")) {
                send();
            }
        }
    }

    boolean connect(String host, int port) {
        socket = null;

        try {
            socket = new Socket(InetAddress.getByName(host), port);
            // socket.setSoTimeout(10000);

            if (socket.getSoLinger() != -1) {
                socket.setSoLinger(true, 5);
            }
            if (!socket.getTcpNoDelay()) {
                socket.setTcpNoDelay(true);
            }

            display.appendText(String.format("Connect to Socket[addr=%s, port=%d, localhost=%d]\n"
                    , socket.getInetAddress(), socket.getPort(), socket.getLocalPort()));

            connection = new ConnectionWrapper(socket);
            outputStream = connection.createObjectOStreams();
            connection.createObjectIStreams();
            return true;

        } catch (IOException exception) {
            Platform.runLater(() -> display.appendText("ERROR: " + exception.getMessage() + ": server is not available. Check port or restart server."));
            return false;
        }
    }

    public void send() {
        String sendMessage = message.getText();

        try {
            Platform.runLater(() -> display.appendText(sendMessage + ChatProtocolConstants.LINE_TERMINATOR));
            outputStream.writeObject(ChatProtocolConstants.DISPLACEMENT + sendMessage + ChatProtocolConstants.LINE_TERMINATOR);
            outputStream.flush();
            message.setText("");
        } catch (IOException exception) {
            enableConnectionButton();
            Platform.runLater(() -> display.appendText("\n" + exception.getMessage()));
        }
    }
}

