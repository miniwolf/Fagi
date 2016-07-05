/*
 * Copyright (c) 2014. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig.
 */

package com.fagi.model;

public class VoiceMessage extends Message<byte[]> {
    /**
     * Contains voice data.
     */
    private byte[] data;

    public VoiceMessage(byte[] data, String sender, long conversationID) {
        super(sender, conversationID);
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }
}
