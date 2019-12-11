package sample;

/****************************************************************************************
 Filename:Accessible.java
 Author: Andressa Pessoa de Araujo Machado [040923007]
 Course: CST8221 - Java Applications, Lab Section 302
 Assignment Number:Assignment#2, Part2
 Due Date: 2019/12/06
 Submission 2019/12/06
 Professor's Name: Daniel Cormier.
 Purpose: Abstract class containing getDisplay(), and closeChat() methods
 ***************************************************************************************/

import javafx.scene.control.TextArea;

/**
 * This class is a abstract class inherited by ChatRunnable, ClientChatUI, and ServerChatUI classes
 *
 * @author Andressa Machado
 * @version 1.0
 * @see javafx.scene.control.TextArea
 * @since jdk1.8.0_221
 */
public interface Accessible {

    TextArea getDisplay();

    void closeChat();
}
