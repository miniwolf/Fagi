/*
 * Copyright (c) 2014. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig.
 */

package model;

public class VoiceMessage extends Message<byte[]> {
    /**
     * Contains voice data.
     */
    private byte[] data;

    public VoiceMessage(byte[] data, String sender, String receiver) {
        super(sender, receiver);
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }
}
