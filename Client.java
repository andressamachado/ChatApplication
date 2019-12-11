package sample;

/****************************************************************************************
 Filename: Client.java
 Author: Andressa Pessoa de Araujo Machado [040923007]
 Course: CST8221 - Java Applications, Lab Section 302
 Assignment Number:Assignment#2, Part2
 Due Date: 2019/12/06
 Submission 2019/12/06
 Professor's Name: Daniel Cormier.
 Purpose: Class containing main to launch the ClientChatUI (Client User Interface)
 ***************************************************************************************/

import javafx.application.Application;

/**
 * Class containing main to launch the ClientChatUI (Client User Interface)
 *
 * @author Andressa Machado
 * @version 1.0
 * @since jdk1.8.0_221
 */
public class Client {
    public static void main(String[] args) {
        Application.launch(ClientChatUI.class);
    }
}
