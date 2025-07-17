package com.fagi.encryption;

import com.fagi.logging.FagiLogger;
import com.fagi.logging.FagiLoggerFactory;

import java.io.File;
import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * Created by Marcus on 04-06-2016.
 */
public class Encryption {
    private static final FagiLogger LOGGER = FagiLoggerFactory.createLogger(Encryption.class);
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
                LOGGER.error(
                        e,
                        () -> "Could not save RSA key pair."
                );
            }
        } else {
            try {
                KeyPair key = KeyStorage.LoadKeyPair("RSA");
                this.rsa = new RSA(key);
            } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
                LOGGER.error(
                        e,
                        () -> "Could not load RSA key pair."
                );
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
