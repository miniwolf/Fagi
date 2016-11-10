/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.model.messages.message;

import com.fagi.model.messages.Access;
import com.fagi.model.messages.InGoingMessages;

/**
 * @author miniwolf
 */
public class TextMessage implements InGoingMessages, TextAccess {
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
    public Access getAccess() {
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
}
