package com.fagi.model;/*
 * Copyright (c) 2011. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig
 * Message.java
 *
 * Serializable object to send messages to server.
 */

import java.io.Serializable;

public abstract class Message<E> implements Serializable {
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

    public Message(String sender, String receiver) {
        this.sender = sender;
        this.receiver = receiver;
        systemMessage = false;
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

    public abstract E getData();
}