package model;/*
 * Copyright (c) 2011. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig
 * Message.java
 *
 * Serializable object to send messages to server.
 */

import java.io.Serializable;

public class Message implements Serializable {
    /**
     * Containing the message text
     */
    private final String text;
    /**
     * The message sender
     */
    private final String sender;
    /**
     * The message receiver
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