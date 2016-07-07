/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.model.messages.message;

import com.fagi.model.messages.Access;
import com.fagi.model.messages.InGoingMessages;

public class VoiceMessage implements InGoingMessages, VoiceAccess {
    /**
     * Contains voice data.
     */
    private byte[] data;

    private Message message;

    public VoiceMessage(byte[] data, String sender, long conversationID) {
        message = new DefaultMessage(sender, conversationID);
        this.data = data;
    }

    @Override
    public byte[] getData() {
        return data;
    }

    @Override
    public Message getMessage() {
        return message;
    }

    @Override
    public Access getAccess() {
        return this;
    }
}
