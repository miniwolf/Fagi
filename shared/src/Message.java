/*
 * COPYRIGHT © Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig 2011
 * Message.java
 *
 * Serializable object to send messages to server.
 */

import java.io.Serializable;

class Message implements Serializable {
    /**
     * Containing the message text
     */
    private final String text;
    /**
     * The message sender
     */
    private final String sender;
    /**
     * The message reciever
     */
    private final String receiver;
    /**
     * TODO: Deprecated?
     */
    private final boolean systemMessage;

    public Message(String sender, String text, String receiver) {
        this.text = text;
        this.sender = sender;
        this.receiver = receiver;
        systemMessage = false;
    }

    public Message(String text, boolean systemMessage) {
        this.text = text;
        this.systemMessage = systemMessage;
        this.sender = "";
        this.receiver = "";
    }

    public String getMessage() {
        return text;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public boolean isSystemMessage() {
        return systemMessage;
    }
}