package com.fagi.encryption;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by Marcus on 04-06-2016.
 */
public class KeyStorage {
    private static final String KEYSFOLDER = "config/keys";
    public static final String PUBLICKEYFOLDER = KEYSFOLDER + "/public.key";
    public static final String PRIVATEKEYFOLDER = KEYSFOLDER + "/private.key";

    public static void SaveKeyPair(KeyPair keyPair) throws IOException {
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();

        File config = new File("config");
        if (!config.exists()) {
            config.mkdir();
        }

        File keys = new File("config/keys");
        if (!keys.exists()) {
            keys.mkdir();
        }

        // Store Public Key.
        File pk = new File(PUBLICKEYFOLDER);
        if(!pk.exists()) {
            pk.createNewFile();
        }
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(
                publicKey.getEncoded());
        FileOutputStream fos = new FileOutputStream(PUBLICKEYFOLDER);
        fos.write(x509EncodedKeySpec.getEncoded());
        fos.close();

        // Store Private Key.
        File sk = new File(PRIVATEKEYFOLDER);
        if(!sk.exists()) {
            sk.createNewFile();
        }
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(
                privateKey.getEncoded());
        fos = new FileOutputStream(PRIVATEKEYFOLDER);
        fos.write(pkcs8EncodedKeySpec.getEncoded());
        fos.close();
    }

    public static KeyPair LoadKeyPair(String algorithm)
            throws IOException, NoSuchAlgorithmException,
            InvalidKeySpecException {
        // Read Public Key.
        File filePublicKey = new File(PUBLICKEYFOLDER);
        FileInputStream fis = new FileInputStream(PUBLICKEYFOLDER);
        byte[] encodedPublicKey = new byte[(int) filePublicKey.length()];
        fis.read(encodedPublicKey);
        fis.close();

        // Read Private Key.
        File filePrivateKey = new File(PRIVATEKEYFOLDER);
        fis = new FileInputStream(PRIVATEKEYFOLDER);
        byte[] encodedPrivateKey = new byte[(int) filePrivateKey.length()];
        try {
            fis.read(encodedPrivateKey);
        } catch (IOException e) {
            e.printStackTrace();
        }
        fis.close();

        // Generate KeyPair.
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(
                encodedPublicKey);
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(
                encodedPrivateKey);
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

        return new KeyPair(publicKey, privateKey);
    }
}
