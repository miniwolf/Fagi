package com.fagi.encryption;

import javax.crypto.SecretKey;

/**
 * Created by Marcus on 04-06-2016.
 */
public record AESKey(SecretKey key) implements Key<SecretKey> {
}
