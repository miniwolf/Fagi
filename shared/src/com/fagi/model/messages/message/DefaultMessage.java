/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.model.messages.message;

/**
 * @author miniwolf
 */
public class DefaultMessage implements Message {
    private String sender;
    private long conversationID;

    public DefaultMessage(String sender, long conversationID) {
        this.sender = sender;
        this.conversationID = conversationID;
    }

    @Override
    public String getSender() {
        return sender;
    }

    @Override
    public long getConversationID() {
        return conversationID;
    }
}
