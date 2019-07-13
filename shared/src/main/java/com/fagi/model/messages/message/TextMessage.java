/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.model.messages.message;

import com.fagi.model.messages.Access;
import com.fagi.model.messages.InGoingMessages;

/**
 * Sending messages in a conversation are stored using these elements.
 * Information about sending time and sender is stored in a DefaultMessageInfo.
 *
 * @author miniwolf
 */
public class TextMessage implements InGoingMessages<String>, TextAccess, Comparable<TextMessage> {
    private DefaultMessageInfo message;

    /**
     * Contains text message.
     */
    private String data;

    public TextMessage(String data, String sender, long conversationID) {
        this.data = data;
        message = new DefaultMessageInfo(sender, conversationID);
    }

    @Override
    public Access<String> getAccess() {
        return this;
    }

    @Override
    public MessageInfo getMessageInfo() {
        return message;
    }

    @Override
    public String getData() {
        return data;
    }

    @Override
    public boolean equals(Object other) {
        if ( this == other ) {
            return true;
        }
        if ( !(other instanceof TextMessage) ) {
            return false;
        }

        TextMessage message1 = (TextMessage) other;

        return message.equals(message1.message) && data.equals(message1.data);
    }

    @Override
    public int hashCode() {
        int result = message.hashCode();
        result = 31 * result + data.hashCode();
        return result;
    }

    @Override
    public int compareTo(TextMessage other) {
        return message.getTimestamp().compareTo(other.getMessageInfo().getTimestamp());
    }
}
