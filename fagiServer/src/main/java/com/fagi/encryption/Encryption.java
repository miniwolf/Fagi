package com.fagi.encryption;

import java.io.File;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

/**
 * Created by Marcus on 04-06-2016.
 */
public class Encryption {
    private static Encryption instance;

    private RSA rsa;

    private Encryption() {
        File f = new File(KeyStorage.PUBLICKEYFILE);
        if (!f.exists()) {
            this.rsa = new RSA();
            KeyPair key = (KeyPair) rsa.getKey().getKey();
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
            instance = new Encryption();
        }
        return instance;
    }

    public RSA getRSA() {
        return rsa;
    }
}
