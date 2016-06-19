/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.model.messages.message;

/**
 * @author miniwolf
 */
public class DefaultMessage implements Message {
    private String sender;
    private String receiver;

    public DefaultMessage(String sender, String receiver) {
        this.sender = sender;
        this.receiver = receiver;
    }

    @Override
    public String getSender() {
        return sender;
    }

    @Override
    public String getReceiver() {
        return receiver;
    }
}
