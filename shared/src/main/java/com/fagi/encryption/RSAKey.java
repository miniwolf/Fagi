package com.fagi.encryption;

import java.security.KeyPair;

/**
 * Created by Marcus on 30-05-2016.
 */
public class RSAKey implements Key<KeyPair> {
    private final KeyPair k;

    public RSAKey(KeyPair k) {
        this.k = k;
    }

    @Override
    public KeyPair getKey() {
        return k;
    }
}
