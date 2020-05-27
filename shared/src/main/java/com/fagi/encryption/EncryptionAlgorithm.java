package com.fagi.encryption;

/**
 * Created by Marcus on 31-05-2016.
 */
public interface EncryptionAlgorithm<K> {
    byte[] encrypt(byte[] msg);

    byte[] decrypt(byte[] cipherText);

    void generateKey(int keyLength);

    Key getKey();

    void setEncryptionKey(K key);
}
