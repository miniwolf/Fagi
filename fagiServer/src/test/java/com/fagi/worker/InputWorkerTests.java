package com.fagi.worker;

import com.fagi.encryption.AES;
import com.fagi.encryption.AESKey;
import com.fagi.encryption.Conversion;
import com.fagi.encryption.Encryption;
import com.fagi.encryption.RSA;
import com.fagi.handler.ConversationHandler;
import com.fagi.handler.InputHandler;
import com.fagi.model.Data;
import com.fagi.model.Login;
import com.fagi.model.Session;
import com.fagi.model.UserNameAvailableRequest;
import com.fagi.responses.AllIsWell;
import com.fagi.util.OutputAgentTestUtil;
import com.fagi.util.RunOnceStrategy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

public class InputWorkerTests {
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

    @AfterEach
    void tearDown() {
        System.setErr(System.err);
        System.setOut(System.out);
    }

    @Test
    void whenInputWorkerIsCreated_ThenStartingThreadMessagesIsPrintedToConsole() {
        var outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        new InputWorker(
                mockObjectInputStream,
                outputWorker,
                conversationHandler,
                data
        );

        String consoleOutput = outContent.toString();
        assertTrue(consoleOutput.contains("Starting an input thread"));
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
    void givenRunningIsSetToFalse_WhenWorkerIsRunning_ThenShouldNotPrintRunningToConsole() {
        var outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        inputWorker.setRunning(false);

        inputWorker.run();

        String consoleOutput = outContent.toString();
        assertFalse(consoleOutput.contains("Running"));
    }

    @Test
    void givenRunningIsSetToFalse_WhenWorkerIsRunning_ThenShouldPrintClosingInputToConsole() {
        var outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        inputWorker.setRunning(false);

        inputWorker.run();

        String consoleOutput = outContent.toString();
        assertTrue(consoleOutput.contains("Closing input"));
    }

    @Test
    void givenRunningIsSetToTrue_WhenWorkerIsRunning_ThenShouldPrintRunningToConsole() {
        try (var mockedConversion = Mockito.mockStatic(Conversion.class)) {
            mockedConversion
                    .when(() -> Conversion.convertFromBytes(any()))
                    .thenReturn("loginRequest");

            var mockAes = Mockito.mock(AES.class);
            when(mockAes.decrypt(any())).thenReturn("decrypted".getBytes());

            var outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));

            inputWorker.setIsWorkerRunningStrategy(new RunOnceStrategy());
            inputWorker.setSessionCreated(true);
            inputWorker.setAes(mockAes);

            inputWorker.run();

            String consoleOutput = outContent.toString();
            assertTrue(consoleOutput.contains("Running"));
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

            inputWorker.setIsWorkerRunningStrategy(new RunOnceStrategy());

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

            inputWorker.setIsWorkerRunningStrategy(new RunOnceStrategy());
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
    void givenConversionFailsToConvertByteArrayToObject_WhenWorkerReceivesEncryptedObject_ThenSystemErrorShouldContainClassNotFoundException() {
        try (var mockedConversion = Mockito.mockStatic(Conversion.class)) {
            byte[] decryptedInput = "decryptedData".getBytes();
            var mockedAES = Mockito.mock(AES.class);
            when(mockedAES.decrypt(any())).thenReturn(decryptedInput);

            mockedConversion
                    .when(() -> Conversion.convertFromBytes(any()))
                    .thenThrow(new ClassNotFoundException());


            var outContent = new ByteArrayOutputStream();
            System.setErr(new PrintStream(outContent));

            inputWorker.setIsWorkerRunningStrategy(new RunOnceStrategy());
            inputWorker.setSessionCreated(true);
            inputWorker.setAes(mockedAES);

            inputWorker.run();

            String consoleOutput = outContent.toString();
            assertTrue(consoleOutput.contains("java.lang.ClassNotFoundException"));
        }
    }

    @Test
    void givenConversionFailsWithIo_WhenWorkerReceivesEncryptedObject_ThenSystemErrorShouldContainIOException() {
        try (var mockedConversion = Mockito.mockStatic(Conversion.class)) {
            byte[] decryptedInput = "decryptedData".getBytes();
            var mockedAES = Mockito.mock(AES.class);
            when(mockedAES.decrypt(ENCRYPTED_DATA)).thenReturn(decryptedInput);

            mockedConversion
                    .when(() -> Conversion.convertFromBytes(any()))
                    .thenThrow(new IOException());


            var outContent = new ByteArrayOutputStream();
            System.setErr(new PrintStream(outContent));

            inputWorker.setIsWorkerRunningStrategy(new RunOnceStrategy());
            inputWorker.setSessionCreated(true);
            inputWorker.setAes(mockedAES);

            inputWorker.run();

            String consoleOutput = outContent.toString();
            assertTrue(consoleOutput.contains("java.io.IOException"));
        }
    }

    @Test
    void givenSocketException_WhenRunning_ThenShouldHandleUserLogoutGracefully() throws IOException, ClassNotFoundException {
        var username = "my username";
        when(mockObjectInputStream.readObject()).thenThrow(new SocketException());

        var outContent = new ByteArrayOutputStream();
        var errorContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errorContent));
        System.setOut(new PrintStream(outContent));

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
                .setRunning(false);

        Assertions.assertAll(
                () -> Assertions.assertFalse(inputWorker.isRunning()),
                () -> Assertions.assertTrue(outContent
                                                    .toString()
                                                    .contains("Logging out user " + username)),
                () -> Assertions.assertFalse(errorContent
                                                     .toString()
                                                     .contains("java.net.SocketException"))
        );
    }

    @Test
    void givenEOFException_WhenRunning_ThenShouldHandleUserLogoutGracefully() throws IOException, ClassNotFoundException {
        var username = "my username";
        when(mockObjectInputStream.readObject()).thenThrow(new EOFException());

        var outContent = new ByteArrayOutputStream();
        var errorContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errorContent));
        System.setOut(new PrintStream(outContent));

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
                .setRunning(false);

        Assertions.assertAll(
                () -> Assertions.assertFalse(inputWorker.isRunning()),
                () -> Assertions.assertTrue(outContent
                                                    .toString()
                                                    .contains("Logging out user " + username)),
                () -> Assertions.assertFalse(errorContent
                                                     .toString()
                                                     .contains("java.io.EOFException"))
        );
    }

    @Test
    void givenUnexpectedException_WhenRunning_ThenShouldHandleUserLogoutGracefullyButWithStacktrace() throws IOException, ClassNotFoundException {
        var username = "my username";
        when(mockObjectInputStream.readObject()).thenThrow(new SocketTimeoutException());

        var outContent = new ByteArrayOutputStream();
        var errorContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errorContent));
        System.setOut(new PrintStream(outContent));

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
                .setRunning(false);

        Assertions.assertAll(
                () -> Assertions.assertFalse(inputWorker.isRunning()),
                () -> Assertions.assertTrue(outContent
                                                    .toString()
                                                    .contains("Logging out user " + username)),
                () -> Assertions.assertTrue(outContent
                                                    .toString()
                                                    .contains("Something went wrong in a input worker while loop ")),
                () -> Assertions.assertTrue(outContent
                                                    .toString()
                                                    .contains("java.net.SocketTimeoutException")),
                () -> Assertions.assertTrue(errorContent
                                                    .toString()
                                                    .contains("java.net.SocketTimeoutException"))
        );
    }

    @Test
    void givenRequestIsAJavaObject_WhenSendingUserNameAvailableRequest_ThenOutputWorkerShouldReceiveAllIsWellResponse() throws IOException, ClassNotFoundException {
        when(data.getUser(any())).thenReturn(null);
        when(mockObjectInputStream.readObject()).thenReturn(new UserNameAvailableRequest("bob"));

        inputWorker.setIsWorkerRunningStrategy(new RunOnceStrategy());

        inputWorker.run();

        OutputAgentTestUtil.assertOutputAgentReceivedResponseClass(
                outputWorker,
                AllIsWell.class
        );
    }
}
