package com.fagi.encryption;

import com.fagi.utility.Logger;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Marcus on 04-06-2016.
 */
public class AES implements EncryptionAlgorithm<AESKey> {
    byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    IvParameterSpec ivspec = new IvParameterSpec(iv);
    private AESKey key;

    public AES() {
        this(128);
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
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key.getKey(), ivspec);
            return cipher.doFinal(msg);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | InvalidKeyException | IllegalBlockSizeException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            Logger.logStackTrace(e);
        }
        return null;
    }

    @Override
    public byte[] decrypt(byte[] cipherText) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key.getKey(), ivspec);
            return cipher.doFinal(cipherText);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException e) {
            e.printStackTrace();
            Logger.logStackTrace(e);
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
            Logger.logStackTrace(e);
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