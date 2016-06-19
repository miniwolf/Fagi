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
    private Message message;

    /**
     * Contains text message.
     */
    private String data;

    public TextMessage(String data, String sender, String receiver) {
        this.data = data;
        message = new DefaultMessage(sender, receiver);
    }

    @Override
    public Access getAccess() {
        return this;
    }

    public Message getMessage() {
        return message;
    }

    @Override
    public String getData() {
        return data;
    }
}
