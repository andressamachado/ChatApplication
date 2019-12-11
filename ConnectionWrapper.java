package sample;

/****************************************************************************************
 Filename: ConnectionWrapper.java
 Author: Andressa Pessoa de Araujo Machado [040923007]
 Course: CST8221 - Java Applications, Lab Section 302
 Assignment Number:Assignment#2, Part2
 Due Date: 2019/12/06
 Submission 2019/12/06
 Professor's Name: Daniel Cormier.
 Purpose: Class to control the socket and the input and output streams
 ***************************************************************************************/

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ConnectionWrapper {
    ObjectOutputStream outputStream;
    ObjectInputStream inputStream;
    Socket socket;

    public ConnectionWrapper(Socket socket) {
        this.socket = socket;
    }

    public ObjectOutputStream getOutputStream() {
        return outputStream;
    }

    public ObjectInputStream getInputStream() {
        return inputStream;
    }

    public Socket getSocket() {
        return socket;
    }

    public ObjectInputStream createObjectIStreams() throws IOException {
        inputStream = new ObjectInputStream(socket.getInputStream());
        return inputStream;
    }

    public ObjectOutputStream createObjectOStreams() throws IOException {
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        outputStream.flush();
        return outputStream;
    }

    public void createStreams() throws IOException {
        inputStream = new ObjectInputStream(socket.getInputStream());
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        outputStream.flush();
    }

    public void closeConnection() throws IOException {
        if (outputStream != null) {
            outputStream.close();
        }
        if (inputStream != null) {
            inputStream.close();
        }
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }
}
