package com.fagi.encryption;

import javax.crypto.SecretKey;

/**
 * Created by Marcus on 04-06-2016.
 */
public class AESKey implements Key<SecretKey> {

    private final SecretKey key;

    public AESKey(SecretKey key) {
        this.key = key;
    }

    @Override
    public SecretKey getKey() {
        return key;
    }
}
