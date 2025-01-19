package com.fagi.encryption;

import java.io.File;
import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * Created by Marcus on 04-06-2016.
 */
public class Encryption {
    private static Encryption instance;

    private RSA rsa;

    Encryption(String publicKeyPath) {
        File f = new File(publicKeyPath);
        if (!f.exists()) {
            this.rsa = new RSA();
            KeyPair key = (KeyPair) rsa
                    .getKey()
                    .key();
            try {
                KeyStorage.SaveKeyPair(key);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                KeyPair key = KeyStorage.LoadKeyPair("RSA");
                this.rsa = new RSA(key);
            } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
                e.printStackTrace();
            }
        }

    }

    public static Encryption getInstance() {
        if (instance == null) {
            instance = new Encryption(KeyStorage.PUBLICKEYFILE);
        }
        return instance;
    }

    public RSA getRSA() {
        return rsa;
    }

    /**
     * This method allow for resting the singleton instance. Should only be used in tests.
     */
    static void resetInstance() {
        instance = null;
    }
}
