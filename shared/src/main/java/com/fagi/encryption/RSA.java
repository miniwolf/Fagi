package com.fagi.encryption;

import com.fagi.utility.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Objects;

/**
 * Created by Marcus on 30-05-2016.
 */
public class RSA implements EncryptionAlgorithm<RSAKey> {
    private RSAKey key;
    // TODO: Remove this as it is redundant. Trello task: https://trello.com/c/JZynEGUQ
    private PublicKey encryptionKey;

    public RSA() {
        File f = new File(KeyStorage.PUBLICKEYFILE);
        if (f.exists()) {
            try {
                key = new RSAKey(KeyStorage.LoadKeyPair("RSA"));
            } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
                e.printStackTrace();
                Logger.logStackTrace(e);
            }
        } else {
            generateKey(4096);
        }
    }

    public RSA(int keyLength) {
        generateKey(keyLength);
    }

    public RSA(KeyPair key) {
        this.key = new RSAKey(key);
        this.encryptionKey = key.getPublic();
    }

    public RSA(RSAKey key) {
        this.key = key;
        this.encryptionKey = key
                .key()
                .getPublic();
    }

    @Override
    public byte[] encrypt(byte[] msg) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, encryptionKey);
            return cipher.doFinal(msg);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
            Logger.logStackTrace(e);
        }
        return null;
    }

    @Override
    public byte[] decrypt(byte[] cipherText) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE,
                        key
                                .key()
                                .getPrivate()
            );
            return cipher.doFinal(cipherText);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
            e.printStackTrace();
            Logger.logStackTrace(e);
        }
        return null;
    }

    @Override
    public void generateKey(int keyLength) {
        try {
            KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
            keygen.initialize(keyLength);
            key = new RSAKey(keygen.generateKeyPair());
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
    public void setEncryptionKey(RSAKey key) {
        this.encryptionKey = key
                .key()
                .getPublic();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RSA rsa = (RSA) o;
        return Objects.equals(
                key
                        .key()
                        .getPrivate(),
                rsa.key
                        .key()
                        .getPrivate()
        ) && Objects.equals(
                key
                        .key()
                        .getPublic(),
                rsa.key
                        .key()
                        .getPublic()
        ) && Objects.equals(
                encryptionKey,
                rsa.encryptionKey
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                key,
                encryptionKey
        );
    }
}