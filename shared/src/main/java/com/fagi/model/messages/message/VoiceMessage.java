/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.model.messages.message;

import com.fagi.model.messages.Access;
import com.fagi.model.messages.InGoingMessages;

public class VoiceMessage implements InGoingMessages<byte[]>, VoiceAccess {
    /**
     * Contains voice data.
     */
    private byte[] data;

    private MessageInfo messageInfo;

    public VoiceMessage(byte[] data, String sender, long conversationID) {
        messageInfo = new DefaultMessageInfo(sender, conversationID);
        this.data = data;
    }

    @Override
    public byte[] getData() {
        return data;
    }

    public MessageInfo getMessageInfo() {
        return messageInfo;
    }

    @Override
    public Access<byte[]> getAccess() {
        return this;
    }
}
