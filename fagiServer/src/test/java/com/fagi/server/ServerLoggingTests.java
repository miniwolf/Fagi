package com.fagi.server;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.nio.file.Files;

class ServerLoggingTests extends ServerTests {
    @AfterEach
    void tearDown() {
        System.setOut(System.out);
        System.setErr(System.err);
    }

    @Test
    void givenFileWriteGivesIOException_ThenShouldPrintErrorToErrorConsole() {
        try (var mockedFiles = Mockito.mockStatic(Files.class)) {
            var outContent = new ByteArrayOutputStream();
            System.setErr(new PrintStream(outContent));

            mockedFiles
                    .when(() -> Files.write(
                            Mockito.any(),
                            Mockito.any(byte[].class),
                            Mockito.any()
                    ))
                    .thenThrow(new IOException());

            new Server(
                    serverPort,
                    data
            );

            String consoleOutput = outContent.toString();
            Assertions.assertTrue(consoleOutput.contains("java.io.IOException"));
        }
    }

    @Test
    void givenServerSocketAcceptThrowsIOException_ThenShouldPrintErrorToSysOutButNotSysError() throws IOException {
        var serverSocket = Mockito.mock(ServerSocket.class);
        var outContent = new ByteArrayOutputStream();
        var outErrorContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(outErrorContent));

        Mockito
                .when(serverSocket.accept())
                .thenThrow(new IOException());

        var server = new Server(
                serverPort,
                data
        );
        server.start(serverSocket);

        Assertions.assertAll(
                () -> Assertions.assertTrue(outContent
                                                    .toString()
                                                    .contains("Error in server loop exception = ")),
                () -> Assertions.assertTrue(outContent
                                                    .toString()
                                                    .contains("java.io.IOException")),
                () -> Assertions.assertFalse(outErrorContent
                                                     .toString()
                                                     .contains("Error in server loop exception = ")),
                () -> Assertions.assertFalse(outErrorContent
                                                     .toString()
                                                     .contains("java.io.IOException")),
                () -> Assertions.assertFalse(server.isRunning())
        );
    }

    @Test
    void givenServerSocketCloseThrowsIOException_ThenShouldPrintErrorToSysErrorButNotSysOut() throws IOException {
        var serverSocket = Mockito.mock(ServerSocket.class);
        var outContent = new ByteArrayOutputStream();
        var outErrorContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(outErrorContent));

        Mockito
                .doThrow(new IOException())
                .when(serverSocket)
                .close();

        var server = new Server(
                serverPort,
                data
        );
        server.setRunning(false);
        server.start(serverSocket);

        Assertions.assertAll(
                () -> Assertions.assertFalse(outContent
                                                     .toString()
                                                     .contains("java.io.IOException")),
                () -> Assertions.assertTrue(outErrorContent
                                                    .toString()
                                                    .contains("java.io.IOException")),
                () -> Assertions.assertFalse(server.isRunning())
        );
    }

    @Test
    void whenServerStarts_ThenStartingServerIsPrintedInSysOut() {
        var outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        var server = new Server(
                serverPort,
                data
        );
        server.setRunning(false);
        server.start(null);

        Assertions.assertTrue(outContent
                                      .toString()
                                      .contains("Starting Server"));
    }

    @Test
    void whenServerStops_ThenStoppingServerIsPrintedInSysOut() {
        var outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        var server = new Server(
                serverPort,
                data
        );
        server.setRunning(false);
        server.start(null);

        Assertions.assertTrue(outContent
                                      .toString()
                                      .contains("Stopping Server"));
    }
}
