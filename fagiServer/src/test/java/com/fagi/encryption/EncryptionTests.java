package com.fagi.encryption;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

class EncryptionTests {
    private static final String PUBLIC_KEY_PATH = "build/test/data/encryption_test/public.key";

    @AfterEach
    void tearDown() {
        System.setErr(System.err);
        deleteFileAndFolder(PUBLIC_KEY_PATH);
        deleteFileAndFolder(KeyStorage.PUBLICKEYFILE);
        deleteFileAndFolder(KeyStorage.PRIVATEKEYFILE);
        Encryption.resetInstance();
    }

    @Test
    public void constructorLoadKeyPairThrowsExceptionIOExceptionWhenLoadKeyThrowsThat() throws Exception {
        try (var mockedKeyStorage = Mockito.mockStatic(KeyStorage.class)) {
            mockedKeyStorage
                    .when(() -> KeyStorage.LoadKeyPair("RSA"))
                    .thenThrow(new IOException());

            createPublicKeyFile();

            var outContent = new ByteArrayOutputStream();
            System.setErr(new PrintStream(outContent));

            new Encryption(PUBLIC_KEY_PATH);

            String consoleOutput = outContent.toString();
            assertTrue(consoleOutput.contains("IOException"));
        }
    }

    @Test
    public void constructorLoadKeyPairThrowsNoSuchAlgorithmExceptionWhenLoadKeyThrowsThat() throws Exception {
        try (var mockedKeyStorage = Mockito.mockStatic(KeyStorage.class)) {
            mockedKeyStorage
                    .when(() -> KeyStorage.LoadKeyPair("RSA"))
                    .thenThrow(new NoSuchAlgorithmException());

            createPublicKeyFile();

            var outContent = new ByteArrayOutputStream();
            System.setErr(new PrintStream(outContent));

            new Encryption(PUBLIC_KEY_PATH);

            String consoleOutput = outContent.toString();
            assertTrue(consoleOutput.contains("NoSuchAlgorithmException"));
        }
    }

    @Test
    public void constructorLoadKeyPairThrowsInvalidKeySpecExceptionWhenLoadKeyThrowsThat() throws Exception {
        try (var mockedKeyStorage = Mockito.mockStatic(KeyStorage.class)) {
            mockedKeyStorage
                    .when(() -> KeyStorage.LoadKeyPair("RSA"))
                    .thenThrow(new NoSuchAlgorithmException());

            createPublicKeyFile();

            var outContent = new ByteArrayOutputStream();
            System.setErr(new PrintStream(outContent));

            new Encryption(PUBLIC_KEY_PATH);

            String consoleOutput = outContent.toString();
            assertTrue(consoleOutput.contains("InvalidKeySpecException"));
        }
    }

    @Test
    void constructorSaveKeyPairThrowsIOExceptionWhenSaveKeyPairThrowsThat() {
        try (var mockedKeyStorage = Mockito.mockStatic(KeyStorage.class)) {
            mockedKeyStorage
                    .when(() -> KeyStorage.SaveKeyPair(any()))
                    .thenThrow(new IOException());

            var outContent = new ByteArrayOutputStream();
            System.setErr(new PrintStream(outContent));

            new Encryption(PUBLIC_KEY_PATH);

            String consoleOutput = outContent.toString();
            assertTrue(consoleOutput.contains("IOException"));
        }
    }

    @Test
    void constructorCreatesRSAKeyAndTriesToStoreIt() {
        try (var mockedStatic = Mockito.mockStatic(KeyStorage.class)) {
            var encryption = new Encryption(PUBLIC_KEY_PATH);
            mockedStatic.verify(() -> KeyStorage.SaveKeyPair(eq((KeyPair) encryption
                    .getRSA()
                    .getKey()
                    .key())));
        }
    }

    @Test
    void constructorCanLoadRSAKeyFromDisk() throws IOException {
        var rsa = new RSA(1024);
        KeyStorage.SaveKeyPair((KeyPair) rsa
                .getKey()
                .key());

        var encryption = Encryption.getInstance();

        assertEquals(
                rsa,
                encryption.getRSA()
        );
    }

    @Test
    void encryptionInstanceIsASingleton() {
        assertSame(
                Encryption.getInstance(),
                Encryption.getInstance()
        );
    }

    private static void createPublicKeyFile() throws IOException {
        File file = new File(PUBLIC_KEY_PATH);
        if (!file.exists()) {
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                boolean succededInCreatingParentFolders = parentDir.mkdirs();
                assertTrue(
                        succededInCreatingParentFolders,
                        "Could not create parent folders for public key file"
                );
            }

            boolean succededInCreatingPublicKeyFile = file.createNewFile();
            assertTrue(
                    succededInCreatingPublicKeyFile,
                    "Could not create public key file"
            );
        }
    }

    private static void deleteFileAndFolder(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
            file
                    .getParentFile()
                    .delete();
        }
    }
}
