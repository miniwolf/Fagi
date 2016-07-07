/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.model.messages.message;

import java.sql.Timestamp;

/**
 * @author miniwolf
 */
public class DefaultMessage implements Message {
    private String sender;
    private long conversationID;
    private Timestamp timestamp;

    public DefaultMessage(String sender, long conversationID) {
        this.sender = sender;
        this.conversationID = conversationID;
    }

    public DefaultMessage() {}

    @Override
    public String getSender() {
        return sender;
    }

    @Override
    public long getConversationID() {
        return conversationID;
    }

    @Override
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public Timestamp getTimestamp() {
        return timestamp;
    }
}
