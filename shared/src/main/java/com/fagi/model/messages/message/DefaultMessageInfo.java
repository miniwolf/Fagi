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
    private Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    private long currentTime;

    public DefaultMessageInfo(
            String sender,
            long conversationID) {
        this.sender = sender;
        this.conversationID = conversationID;
        currentTime = System.currentTimeMillis();
    }

    public DefaultMessageInfo() {
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DefaultMessageInfo that)) {
            return false;
        }

        return conversationID == that.conversationID && sender.equals(that.sender) && that.currentTime == currentTime;
    }

    @Override
    public int hashCode() {
        int result = sender.hashCode();
        result = 31 * result + (int) (conversationID ^ (conversationID >>> 32));
        result = 31 * result + timestamp.hashCode();
        result = 31 * result + Long.hashCode(currentTime);
        return result;
    }
}
