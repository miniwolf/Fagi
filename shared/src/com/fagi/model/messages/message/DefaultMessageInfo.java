/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.model.messages.message;

import java.sql.Timestamp;

/**
 * @author miniwolf
 */
public class DefaultMessageInfo implements MessageInfo {
    private String sender;
    private long conversationID;
    private Timestamp timestamp;

    public DefaultMessageInfo(String sender, long conversationID) {
        this.sender = sender;
        this.conversationID = conversationID;
    }

    public DefaultMessageInfo() {}

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
