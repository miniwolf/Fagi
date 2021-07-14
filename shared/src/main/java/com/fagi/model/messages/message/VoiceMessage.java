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
    private final byte[] data;

    private final MessageInfo messageInfo;

    public VoiceMessage(
            byte[] data,
            String sender,
            long conversationID) {
        messageInfo = new DefaultMessageInfo(sender, conversationID);
        this.data = data;
    }

    @Override
    public byte[] data() {
        return data;
    }

    public MessageInfo getMessageInfo() {
        return messageInfo;
    }

    @Override
    public Access<byte[]> access() {
        return this;
    }
}
