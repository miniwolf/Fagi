/*
 * Copyright (c) 2014. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig.
 */

package model;

public class Voice {
    private byte[] data;

    public Voice(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }
}
