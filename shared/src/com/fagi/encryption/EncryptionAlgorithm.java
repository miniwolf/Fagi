package com.fagi.encryption;

import java.math.BigInteger;

/**
 * Created by Marcus on 31-05-2016.
 */
public interface EncryptionAlgorithm {
    BigInteger encrypt(Key publicKey, byte[] msg);
    byte[] decrypt(BigInteger cipher);
}
