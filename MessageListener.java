/*
 * COPYRIGHT © Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig 2011
 * MessageListener.jav
 *
 * For recieving messages and printing these to the conversation
 */

/**
 * @author miniwolf
 */

import javax.swing.*;

public class MessageListener extends Thread {

    /* Private variables declaration */
    private Communication communication;
    private StringBuilder msg;
    private String currentMsg;
    private JTextArea jConversation;

    public MessageListener() {
	communication = null;
	msg = new StringBuilder();
	currentMsg = "";
	jConversation = null;
    }

    public MessageListener(Communication communication, JTextArea jConversation) {
        this.communication = communication;
        this.jConversation = jConversation;
    }
 
    /** Thread method, sending the text to the JTextArea inside ContactScreen*/
    public void run() {
        while (true) {
            String s = communication.ReceiveMessage();
            jConversation.append(s + "\n");
        }
    }

    /** returns the string */
    public final String getCurrentMessage() {
        return currentMsg;
    }
}
