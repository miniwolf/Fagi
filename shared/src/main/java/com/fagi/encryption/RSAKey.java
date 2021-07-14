package com.fagi.encryption;

import java.security.KeyPair;

/**
 * Created by Marcus on 30-05-2016.
 */
public record RSAKey(KeyPair key) implements Key<KeyPair> {
}
