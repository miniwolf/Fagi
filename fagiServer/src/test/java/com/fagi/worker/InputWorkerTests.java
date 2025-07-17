package com.fagi.worker;

import com.fagi.BaseFagiTest;
import com.fagi.encryption.AES;
import com.fagi.encryption.AESKey;
import com.fagi.encryption.Conversion;
import com.fagi.encryption.Encryption;
import com.fagi.encryption.RSA;
import com.fagi.handler.ConversationHandler;
import com.fagi.handler.InputHandler;
import com.fagi.logging.TestLogLevel;
import com.fagi.logging.TestLogRecord;
import com.fagi.model.Data;
import com.fagi.model.Login;
import com.fagi.model.Session;
import com.fagi.model.UserNameAvailableRequest;
import com.fagi.responses.AllIsWell;
import com.fagi.util.OutputAgentTestUtil;
import com.fagi.util.running.NeverRunStrategy;
import com.fagi.util.running.RunOnceStrategy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

public class InputWorkerTests extends BaseFagiTest {
    private static final byte[] ENCRYPTED_DATA = "encryptedData".getBytes();
    private OutputWorker outputWorker;
    private ConversationHandler conversationHandler;
    private Data data;
    private InputWorker inputWorker;
    private ObjectInputStream mockObjectInputStream;

    @BeforeEach
    void setup() throws IOException, ClassNotFoundException {
        mockObjectInputStream = Mockito.mock(ObjectInputStream.class);
        when(mockObjectInputStream.readObject()).thenReturn(ENCRYPTED_DATA);

        outputWorker = Mockito.mock(OutputWorker.class);
        conversationHandler = Mockito.mock(ConversationHandler.class);
        data = Mockito.mock(Data.class);

        inputWorker = new InputWorker(
                mockObjectInputStream,
                outputWorker,
                conversationHandler,
                data
        );
    }

    @Test
    void whenInputWorkerIsStarted_ThenStartingThreadMessageIsLogged() {
        inputWorker.setIsRunningStrategy(new NeverRunStrategy());

        inputWorker.run();

        List<TestLogRecord<?>> testLogRecords = lookupLogRecordsForClass(InputWorker.class);

        Assertions.assertTrue(testLogRecords
                                      .stream()
                                      .anyMatch(logRecord -> logRecord
                                              .message()
                                              .equals("Starting an input thread")));
    }

    @Test
    void givenInputWorkerReceivedMockValues_WhenConstructorCalled_ThenItsInputHandlerShouldEqualInputHandlerWithMockValues() {
        var inputHandler = new InputHandler(
                inputWorker,
                outputWorker,
                conversationHandler,
                data
        );

        assertEquals(
                inputHandler,
                inputWorker.getInputHandler()
        );
    }

    @Test
    void givenUsernameSetToCharles_ThenUsernameShouldBeCharles() {
        inputWorker.setUsername("Charles");

        assertEquals(
                "Charles",
                inputWorker.getUsername()
        );
    }

    @Test
    void givenRunningIsSetToFalse_WhenWorkerIsRunning_ThenShouldNotLogRunning() {
        inputWorker.setIsRunningStrategy(new NeverRunStrategy());

        inputWorker.run();

        List<TestLogRecord<?>> testLogRecords = lookupLogRecordsForClass(InputWorker.class);

        Assertions.assertFalse(testLogRecords
                                       .stream()
                                       .anyMatch(logRecord -> logRecord
                                               .message()
                                               .equals("Running")));
    }

    @Test
    void givenRunningIsSetToFalse_WhenWorkerIsRunning_ThenShouldLogClosingInput() {
        inputWorker.setIsRunningStrategy(new NeverRunStrategy());

        inputWorker.run();

        List<TestLogRecord<?>> testLogRecords = lookupLogRecordsForClass(InputWorker.class);

        Assertions.assertTrue(testLogRecords
                                      .stream()
                                      .anyMatch(logRecord -> logRecord
                                              .message()
                                              .equals("Closing input.")));
    }

    @Test
    void givenRunningIsSetToTrue_WhenWorkerIsRunning_ThenShouldLogRunning() {
        try (var mockedConversion = Mockito.mockStatic(Conversion.class)) {
            mockedConversion
                    .when(() -> Conversion.convertFromBytes(any()))
                    .thenReturn("loginRequest");

            var mockAes = Mockito.mock(AES.class);
            when(mockAes.decrypt(any())).thenReturn("decrypted".getBytes());

            inputWorker.setIsRunningStrategy(new RunOnceStrategy());
            inputWorker.setSessionCreated(true);
            inputWorker.setAes(mockAes);

            inputWorker.run();


            List<TestLogRecord<?>> testLogRecords = lookupLogRecordsForClass(InputWorker.class);

            Assertions.assertTrue(testLogRecords
                                          .stream()
                                          .anyMatch(logRecord -> logRecord
                                                  .message()
                                                  .equals("Running")));
        }
    }

    @Test
    void givenSessionIsNotCreated_WhenWorkerReceivesEncryptedObject_ThenRSADecryptionIsCalled() {
        try (var mockedEncryptionSingleton = Mockito.mockStatic(Encryption.class);
             var mockedConversion = Mockito.mockStatic(Conversion.class)) {
            byte[] decryptedInput = "decryptedData".getBytes();
            var aes = new AES();
            var sessionRequest = new Session((AESKey) aes.getKey());

            var mockedEncryption = Mockito.mock(Encryption.class);
            var mockedRSA = Mockito.mock(RSA.class);
            when(mockedEncryption.getRSA()).thenReturn(mockedRSA);
            when(mockedRSA.decrypt(any())).thenReturn(decryptedInput);

            mockedEncryptionSingleton
                    .when(Encryption::getInstance)
                    .thenReturn(mockedEncryption);
            mockedConversion
                    .when(() -> Conversion.convertFromBytes(decryptedInput))
                    .thenReturn(sessionRequest);

            inputWorker.setIsRunningStrategy(new RunOnceStrategy());

            Assertions.assertFalse(inputWorker.isSessionCreated());

            inputWorker.run();

            Mockito
                    .verify(
                            mockedRSA,
                            times(1)
                    )
                    .decrypt(ENCRYPTED_DATA);

            Assertions.assertAll(
                    () -> Assertions.assertTrue(inputWorker.isSessionCreated()),
                    () -> Assertions.assertEquals(
                            aes.getKey(),
                            inputWorker
                                    .getAes()
                                    .getKey()
                    )
            );
        }
    }

    @Test
    void givenSessionCreated_WhenWorkerReceivesEncryptedObject_TheAESDecryptionIsCalled() {
        try (var mockedConversion = Mockito.mockStatic(Conversion.class)) {
            byte[] decryptedInput = "decryptedData".getBytes();
            var mockedAES = Mockito.mock(AES.class);
            when(mockedAES.decrypt(ENCRYPTED_DATA)).thenReturn(decryptedInput);

            var loginRequest = new Login(
                    "username",
                    "password"
            );

            mockedConversion
                    .when(() -> Conversion.convertFromBytes(decryptedInput))
                    .thenReturn(loginRequest);

            inputWorker.setIsRunningStrategy(new RunOnceStrategy());
            inputWorker.setSessionCreated(true);
            inputWorker.setAes(mockedAES);

            inputWorker.run();

            Mockito
                    .verify(
                            mockedAES,
                            times(1)
                    )
                    .decrypt(ENCRYPTED_DATA);
        }
    }

    @Test
    void givenConversionFailsToConvertByteArrayToObject_WhenWorkerReceivesEncryptedObject_ThenShouldLogError() {
        try (var mockedConversion = Mockito.mockStatic(Conversion.class)) {
            byte[] decryptedInput = "decryptedData".getBytes();
            var mockedAES = Mockito.mock(AES.class);
            when(mockedAES.decrypt(any())).thenReturn(decryptedInput);

            mockedConversion
                    .when(() -> Conversion.convertFromBytes(any()))
                    .thenThrow(new ClassNotFoundException());

            inputWorker.setIsRunningStrategy(new RunOnceStrategy());
            inputWorker.setSessionCreated(true);
            inputWorker.setAes(mockedAES);

            inputWorker.run();

            List<TestLogRecord<?>> testLogRecords = lookupLogRecordsForClass(
                    InputWorker.class,
                    TestLogLevel.ERROR
            );

            Assertions.assertEquals(
                    1,
                    testLogRecords.size()
            );

            TestLogRecord<?> logRecord = testLogRecords.getFirst();

            Assertions.assertAll(
                    () -> Assertions.assertEquals(
                            "Failed to decrypt or deserialize object.",
                            logRecord.message()
                    ),
                    () -> Assertions.assertEquals(
                            TestLogLevel.ERROR,
                            logRecord.logLevel()
                    ),
                    () -> Assertions.assertInstanceOf(
                            ClassNotFoundException.class,
                            logRecord.throwable()
                    )
            );
        }
    }

    @Test
    void givenConversionFailsWithIo_WhenWorkerReceivesEncryptedObject_ThenShouldLogError() {
        try (var mockedConversion = Mockito.mockStatic(Conversion.class)) {
            byte[] decryptedInput = "decryptedData".getBytes();
            var mockedAES = Mockito.mock(AES.class);
            when(mockedAES.decrypt(ENCRYPTED_DATA)).thenReturn(decryptedInput);

            mockedConversion
                    .when(() -> Conversion.convertFromBytes(any()))
                    .thenThrow(new IOException());

            inputWorker.setIsRunningStrategy(new RunOnceStrategy());
            inputWorker.setSessionCreated(true);
            inputWorker.setAes(mockedAES);

            inputWorker.run();

            List<TestLogRecord<?>> testLogRecords = lookupLogRecordsForClass(
                    InputWorker.class,
                    TestLogLevel.ERROR
            );

            Assertions.assertEquals(
                    1,
                    testLogRecords.size()
            );

            TestLogRecord<?> logRecord = testLogRecords.getFirst();

            Assertions.assertAll(
                    () -> Assertions.assertEquals(
                            "Failed to decrypt or deserialize object.",
                            logRecord.message()
                    ),
                    () -> Assertions.assertEquals(
                            TestLogLevel.ERROR,
                            logRecord.logLevel()
                    ),
                    () -> Assertions.assertInstanceOf(
                            IOException.class,
                            logRecord.throwable()
                    )
            );
        }
    }

    @Test
    void givenSocketException_WhenRunning_ThenShouldHandleUserLogoutGracefully() throws IOException, ClassNotFoundException {
        var username = "my username";
        when(mockObjectInputStream.readObject()).thenThrow(new SocketException());

        inputWorker.setSessionCreated(true);
        inputWorker.setUsername(username);

        inputWorker.run();

        Mockito
                .verify(
                        data,
                        times(1)
                )
                .userLogout(username);

        Mockito
                .verify(
                        outputWorker,
                        times(1)
                )
                .stop();

        List<TestLogRecord<?>> testLogRecords = lookupLogRecordsForClass(InputWorker.class);

        Assertions.assertTrue(testLogRecords
                                      .stream()
                                      .anyMatch(logRecord -> logRecord
                                              .message()
                                              .equals("Logging out user " + username)));
    }

    @Test
    void givenEOFException_WhenRunning_ThenShouldHandleUserLogoutGracefully() throws IOException, ClassNotFoundException {
        var username = "my username";
        when(mockObjectInputStream.readObject()).thenThrow(new EOFException());

        inputWorker.setSessionCreated(true);
        inputWorker.setUsername(username);

        inputWorker.run();

        Mockito
                .verify(
                        data,
                        times(1)
                )
                .userLogout(username);

        Mockito
                .verify(
                        outputWorker,
                        times(1)
                )
                .stop();

        List<TestLogRecord<?>> testLogRecords = lookupLogRecordsForClass(InputWorker.class);

        Assertions.assertTrue(testLogRecords
                                      .stream()
                                      .anyMatch(logRecord -> logRecord
                                              .message()
                                              .equals("Logging out user " + username)));
    }

    @Test
    void givenUnexpectedException_WhenRunning_ThenShouldHandleUserLogoutGracefullyButWithErrorLog() throws IOException, ClassNotFoundException {
        var username = "my username";
        when(mockObjectInputStream.readObject()).thenThrow(new SocketTimeoutException());

        inputWorker.setSessionCreated(true);
        inputWorker.setUsername(username);

        inputWorker.run();

        Mockito
                .verify(
                        data,
                        times(1)
                )
                .userLogout(username);

        Mockito
                .verify(
                        outputWorker,
                        times(1)
                )
                .stop();

        Assertions.assertTrue(lookupLogRecordsForClass(InputWorker.class)
                                      .stream()
                                      .anyMatch(logRecord -> logRecord
                                              .message()
                                              .equals("Logging out user " + username)));


        List<TestLogRecord<?>> testErrorLogRecords = lookupLogRecordsForClass(
                InputWorker.class,
                TestLogLevel.ERROR
        );

        Assertions.assertEquals(
                1,
                testErrorLogRecords.size()
        );

        TestLogRecord<?> errorLogRecord = testErrorLogRecords.getFirst();

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        "Something went wrong in a input worker while loop.",
                        errorLogRecord.message()
                ),
                () -> Assertions.assertEquals(
                        TestLogLevel.ERROR,
                        errorLogRecord.logLevel()
                ),
                () -> Assertions.assertInstanceOf(
                        SocketTimeoutException.class,
                        errorLogRecord.throwable()
                )
        );
    }

    @Test
    void givenRequestIsAJavaObject_WhenSendingUserNameAvailableRequest_ThenOutputWorkerShouldReceiveAllIsWellResponse() throws IOException, ClassNotFoundException {
        when(data.getUser(any())).thenReturn(null);
        when(mockObjectInputStream.readObject()).thenReturn(new UserNameAvailableRequest("bob"));

        inputWorker.setIsRunningStrategy(new RunOnceStrategy());

        inputWorker.run();

        OutputAgentTestUtil.assertOutputAgentReceivedResponseClass(
                outputWorker,
                AllIsWell.class
        );
    }

    @Test
    void givenInputWorkerCreated_ThenShouldBeRunning() {
        Assertions.assertTrue(inputWorker.isRunning());
    }
}
