package sample;
/****************************************************************************************
 Filename: ServerChatUI.java
 Author: Andressa Pessoa de Araujo Machado [040923007]
 Course: CST8221 - Java Applications, Lab Section 302
 Assignment Number:Assignment#2, Part2
 Due Date: 2019/12/06
 Submission 2019/12/06
 Professor's Name: Daniel Cormier.
 Purpose: User interface side of the server
 ***************************************************************************************/

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerChatUI extends Application implements Accessible {
    private Socket socket;
    private String title;
    private TextField serverMessage;
    private Button serverSendButton;
    private TextArea serverChatDisplay;
    private ObjectOutputStream outputStream;
    private ConnectionWrapper connection;
    private Stage primaryStage;

    ServerChatUI(Socket socket, String title) {
        if (socket == null) {
            this.socket = new Socket();
        }
        this.socket = socket;
        this.title = title;
    }

    @Override
    public TextArea getDisplay() {
        return serverChatDisplay;
    }

    @Override
    public void closeChat() {
        try {
            if (!socket.isClosed()) {
                connection.closeConnection();
                System.out.println("Server UI Closed!");
            }
            Platform.runLater(() -> this.primaryStage.close());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStageParam) {
        this.primaryStage = primaryStageParam;
        // stage > scene > container > node
        try {
            primaryStage.setTitle(title);
            primaryStage.setWidth(588);
            primaryStage.setHeight(500);
            primaryStage.setResizable(false);
            //Positioning at the center of the screen
            Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
            primaryStage.setX((primScreenBounds.getWidth() - primaryStage.getWidth()) / 2);
            primaryStage.setY((primScreenBounds.getHeight() - primaryStage.getHeight()) / 2);

            Scene scene = createScene();
            primaryStage.setScene(scene);

            primaryStage.setOnCloseRequest((event) -> {
                try {
                    outputStream.writeObject(ChatProtocolConstants.DISPLACEMENT
                            + ChatProtocolConstants.CHAT_TERMINATOR
                            + ChatProtocolConstants.LINE_TERMINATOR);
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            });
            primaryStage.show();
            runClient();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void runClient() throws IOException {
        connection = new ConnectionWrapper(this.socket);
        outputStream = connection.createObjectOStreams();
        connection.createObjectIStreams();
        Runnable runnable = new ChatRunnable<>(ServerChatUI.this, connection);
        new Thread(runnable).start();
    }

    public Scene createScene() {
        VBox box = new VBox();
        box.setPadding(new Insets(5, 5, 5, 5));

        Controller handler = new Controller();

        //MESSAGE PANE
        serverMessage = new TextField("Type a message");
        serverMessage.setEditable(true);

        serverSendButton = new Button("_Send");
        serverSendButton.setPrefWidth(131);
        serverSendButton.setDisable(false);
        serverSendButton.setOnAction(handler);
        serverSendButton.setMnemonicParsing(true);

        serverMessage.setOnAction(handler);

        GridPane gridPane = new GridPane();
        gridPane.add(serverMessage, 0, 0);
        gridPane.add(serverSendButton, 1, 0);

        gridPane.setBackground(new Background(new BackgroundFill(Color.rgb(242, 242, 242), CornerRadii.EMPTY, Insets.EMPTY)));
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(5, 5, 5, 5));

        ColumnConstraints constraints4 = new ColumnConstraints();
        ColumnConstraints constraints5 = new ColumnConstraints();
        constraints4.setPercentWidth(75);
        constraints5.setPercentWidth(25);
        gridPane.getColumnConstraints().addAll(constraints4, constraints5);
        gridPane.setHgap(5);

        BorderedTitledPane serverMessagePane = new BorderedTitledPane("MESSAGE", Pos.TOP_LEFT, Color.rgb(0, 0, 0), gridPane);

        //CHAT DISPLAY PANE:
        serverChatDisplay = new TextArea();
        serverChatDisplay.setEditable(false);
        serverChatDisplay.setMinHeight(355);

        BorderedTitledPane serverDisplayPane = new BorderedTitledPane("CHAT DISPLAY", Pos.TOP_CENTER, Color.rgb(0, 0, 255), serverChatDisplay);

        box.getChildren().add(serverMessagePane);
        box.getChildren().add(serverDisplayPane);
        box.setSpacing(7);

        return new Scene(box, 300, 300);
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

        @Override
        public void handle(ActionEvent event) {
            if (event.getSource() == serverSendButton) {
                send();
            }
        }

        private void send() {
            String sendMessage = serverMessage.getText();
            Platform.runLater(() -> serverChatDisplay.appendText(sendMessage + ChatProtocolConstants.LINE_TERMINATOR));

            try {
                outputStream.writeObject(ChatProtocolConstants.DISPLACEMENT + sendMessage + ChatProtocolConstants.LINE_TERMINATOR);
                outputStream.flush();
                serverMessage.setText("");
            } catch (IOException exception) {
                Platform.runLater(() -> serverChatDisplay.appendText(exception.getMessage()));
            }
        }
    }
}
