package com.fagi.encryption;

import com.fagi.BaseFagiTest;
import com.fagi.logging.TestLogLevel;
import com.fagi.logging.TestLogRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

class EncryptionTests extends BaseFagiTest {
    private static final String PUBLIC_KEY_PATH = "build/test/data/encryption_test/public.key";

    @AfterEach
    void tearDown() {
        deleteFileAndFolder(PUBLIC_KEY_PATH);
        deleteFileAndFolder(KeyStorage.PUBLICKEYFILE);
        deleteFileAndFolder(KeyStorage.PRIVATEKEYFILE);
        Encryption.resetInstance();
    }

    @Test
    public void whenLoadKeyPairThrowsIOException_ThenConstructorPrintsStacktrace() throws Exception {
        try (var mockedKeyStorage = Mockito.mockStatic(KeyStorage.class)) {
            mockedKeyStorage
                    .when(() -> KeyStorage.LoadKeyPair("RSA"))
                    .thenThrow(new IOException());

            createPublicKeyFile();

            new Encryption(PUBLIC_KEY_PATH);

            List<TestLogRecord<?>> logRecords = lookupLogRecordsForClass(Encryption.class);
            Assertions.assertAll(
                    () -> Assertions.assertEquals(
                            1,
                            logRecords.size()
                    ),
                    () -> Assertions.assertEquals(
                            TestLogLevel.ERROR,
                            logRecords
                                    .getFirst()
                                    .logLevel()
                    ),
                    () -> Assertions.assertEquals(
                            "Could not load RSA key pair.",
                            logRecords
                                    .getFirst()
                                    .message()
                    ),
                    () -> Assertions.assertInstanceOf(
                            IOException.class,
                            logRecords
                                    .getFirst()
                                    .throwable()
                    )
            );
        }
    }

    @Test
    public void whenLoadKeyPairThrowsNoSuchAlgorithmException_ThenConstructorPrintsStacktrace() throws Exception {
        try (var mockedKeyStorage = Mockito.mockStatic(KeyStorage.class)) {
            mockedKeyStorage
                    .when(() -> KeyStorage.LoadKeyPair("RSA"))
                    .thenThrow(new NoSuchAlgorithmException());

            createPublicKeyFile();

            new Encryption(PUBLIC_KEY_PATH);

            List<TestLogRecord<?>> logRecords = lookupLogRecordsForClass(Encryption.class);
            Assertions.assertAll(
                    () -> Assertions.assertEquals(
                            1,
                            logRecords.size()
                    ),
                    () -> Assertions.assertEquals(
                            TestLogLevel.ERROR,
                            logRecords
                                    .getFirst()
                                    .logLevel()
                    ),
                    () -> Assertions.assertEquals(
                            "Could not load RSA key pair.",
                            logRecords
                                    .getFirst()
                                    .message()
                    ),
                    () -> Assertions.assertInstanceOf(
                            NoSuchAlgorithmException.class,
                            logRecords
                                    .getFirst()
                                    .throwable()
                    )
            );
        }
    }

    @Test
    public void whenLoadKeyPairThrowsInvalidKeySpecException_ThenConstructorPrintsStacktrace() throws Exception {
        try (var mockedKeyStorage = Mockito.mockStatic(KeyStorage.class)) {
            mockedKeyStorage
                    .when(() -> KeyStorage.LoadKeyPair("RSA"))
                    .thenThrow(new InvalidKeySpecException());

            createPublicKeyFile();

            new Encryption(PUBLIC_KEY_PATH);

            List<TestLogRecord<?>> logRecords = lookupLogRecordsForClass(Encryption.class);
            Assertions.assertAll(
                    () -> Assertions.assertEquals(
                            1,
                            logRecords.size()
                    ),
                    () -> Assertions.assertEquals(
                            TestLogLevel.ERROR,
                            logRecords
                                    .getFirst()
                                    .logLevel()
                    ),
                    () -> Assertions.assertEquals(
                            "Could not load RSA key pair.",
                            logRecords
                                    .getFirst()
                                    .message()
                    ),
                    () -> Assertions.assertInstanceOf(
                            InvalidKeySpecException.class,
                            logRecords
                                    .getFirst()
                                    .throwable()
                    )
            );
        }
    }

    @Test
    void whenSaveKeyPairThrowsIOException_ThenConstructorPrintsStacktrace() {
        try (var mockedKeyStorage = Mockito.mockStatic(KeyStorage.class)) {
            mockedKeyStorage
                    .when(() -> KeyStorage.SaveKeyPair(any()))
                    .thenThrow(new IOException());

            new Encryption(PUBLIC_KEY_PATH);

            List<TestLogRecord<?>> logRecords = lookupLogRecordsForClass(Encryption.class);
            Assertions.assertAll(
                    () -> Assertions.assertEquals(
                            1,
                            logRecords.size()
                    ),
                    () -> Assertions.assertEquals(
                            TestLogLevel.ERROR,
                            logRecords
                                    .getFirst()
                                    .logLevel()
                    ),
                    () -> Assertions.assertEquals(
                            "Could not save RSA key pair.",
                            logRecords
                                    .getFirst()
                                    .message()
                    ),
                    () -> Assertions.assertInstanceOf(
                            IOException.class,
                            logRecords
                                    .getFirst()
                                    .throwable()
                    )
            );
        }
    }

    @Test
    void givenNoKeyPairFileExists_WhenConstructingEncryption_ThenConstructorAttemptsToStoreRSAKeyPair() {
        try (var mockedStatic = Mockito.mockStatic(KeyStorage.class)) {
            var encryption = new Encryption(PUBLIC_KEY_PATH);
            mockedStatic.verify(() -> KeyStorage.SaveKeyPair(eq((KeyPair) encryption
                    .getRSA()
                    .getKey()
                    .key())));
        }
    }

    @Test
    void givenKeyPairFileExists_WhenConstructingEncryption_ThenConstructorLoadsKeyPairFromDisk() throws IOException {
        var rsa = new RSA(1024);
        rsa.setEncryptionKey((RSAKey) rsa.getKey());
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
