package com.fagi.encryption;

import javax.crypto.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Marcus on 04-06-2016.
 */
public class AES implements EncryptionAlgorithm<AESKey> {
    private AESKey key;

    public AES() {
        this(256);
    }

    public AES(int keyLenght) {
        generateKey(keyLenght);
    }

    public AES(SecretKey key) {
        this.key = new AESKey(key);
    }

    public AES(AESKey key) {
        this.key = key;
    }

    @Override
    public byte[] encrypt(byte[] msg) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key.getKey());
            return cipher.doFinal(msg);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public byte[] decrypt(byte[] cipherText) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.DECRYPT_MODE, key.getKey());
            return cipher.doFinal(cipherText);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void generateKey(int keyLength) {
        try {
            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            keygen.init(keyLength);
            this.key = new AESKey(keygen.generateKey());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Key getKey() {
        return key;
    }

    @Override
    public void setEncryptionKey(AESKey key) {
        this.key = key;
    }
}