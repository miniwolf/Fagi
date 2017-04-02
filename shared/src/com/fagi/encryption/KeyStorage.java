package com.fagi.encryption;

import com.fagi.utility.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by Marcus on 04-06-2016.
 */
public class KeyStorage {
    private static final String KEYSFOLDER = "config/keys";
    public static final String PUBLICKEYFILE = KEYSFOLDER + "/public.key";
    public static final String PRIVATEKEYFILE = KEYSFOLDER + "/private.key";

    public static void SaveKeyPair(KeyPair keyPair) throws IOException {
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();


        // Store Public Key.
        File pk = new File(PUBLICKEYFILE);
        if(!pk.exists()) {
            Path pathToFile = Paths.get(PUBLICKEYFILE);
            Files.createDirectories(pathToFile.getParent());
            Files.createFile(pathToFile);
        }
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(
                publicKey.getEncoded());
        FileOutputStream fos = new FileOutputStream(PUBLICKEYFILE);
        fos.write(x509EncodedKeySpec.getEncoded());
        fos.close();

        // Store Private Key.
        File sk = new File(PRIVATEKEYFILE);
        if(!sk.exists()) {
            Path pathToFile = Paths.get(PRIVATEKEYFILE);
            Files.createDirectories(pathToFile.getParent());
            Files.createFile(pathToFile);
        }
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(
                privateKey.getEncoded());
        fos = new FileOutputStream(PRIVATEKEYFILE);
        fos.write(pkcs8EncodedKeySpec.getEncoded());
        fos.close();
    }

    public static KeyPair LoadKeyPair(String algorithm)
            throws IOException, NoSuchAlgorithmException,
            InvalidKeySpecException {
        // Read Public Key.
        File filePublicKey = new File(PUBLICKEYFILE);
        FileInputStream fis = new FileInputStream(PUBLICKEYFILE);
        byte[] encodedPublicKey = new byte[(int) filePublicKey.length()];
        fis.read(encodedPublicKey);
        fis.close();

        // Read Private Key.
        File filePrivateKey = new File(PRIVATEKEYFILE);
        fis = new FileInputStream(PRIVATEKEYFILE);
        byte[] encodedPrivateKey = new byte[(int) filePrivateKey.length()];
        try {
            fis.read(encodedPrivateKey);
        } catch (IOException e) {
            e.printStackTrace();
            Logger.logStackTrace(e);
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
