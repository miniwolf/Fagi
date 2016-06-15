package com.fagi.model;

import com.fagi.encryption.AESKey;

import java.io.Serializable;

/**
 * Created by Marcus on 05-06-2016.
 */
public class Session implements Serializable {

    private final AESKey key;

    public Session(AESKey key) {
        this.key = key;
    }

    public AESKey getKey() {
        return key;
    }
}