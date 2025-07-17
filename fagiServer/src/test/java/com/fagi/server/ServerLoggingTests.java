package com.fagi.server;

import com.fagi.logging.TestLogLevel;
import com.fagi.logging.TestLogRecord;
import com.fagi.util.running.NeverRunStrategy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.util.List;

class ServerLoggingTests extends ServerTests {
    @Test
    void givenFileWriteGivesIOException_ThenShouldLogError() {
        try (var mockedFiles = Mockito.mockStatic(Files.class)) {
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

            List<TestLogRecord<?>> testLogRecords = lookupLogRecordsForClass(
                    Server.class,
                    TestLogLevel.ERROR
            );

            Assertions.assertEquals(
                    1,
                    testLogRecords.size()
            );

            TestLogRecord<?> logRecord = testLogRecords.getFirst();

            Assertions.assertAll(
                    () -> Assertions.assertEquals(
                            "Could not create server.",
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
    void givenServerSocketAcceptThrowsIOException_ThenShouldLogError() throws IOException {
        var serverSocket = Mockito.mock(ServerSocket.class);

        Mockito
                .when(serverSocket.accept())
                .thenThrow(new IOException());

        var server = new Server(
                serverPort,
                data
        );
        server.start(serverSocket);

        List<TestLogRecord<?>> testLogRecords = lookupLogRecordsForClass(
                Server.class,
                TestLogLevel.ERROR
        );

        Assertions.assertEquals(
                1,
                testLogRecords.size()
        );

        TestLogRecord<?> logRecord = testLogRecords.getFirst();

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        "Error in server loop exception",
                        logRecord.message()
                ),
                () -> Assertions.assertEquals(
                        TestLogLevel.ERROR,
                        logRecord.logLevel()
                ),
                () -> Assertions.assertInstanceOf(
                        IOException.class,
                        logRecord.throwable()
                ),
                () -> Assertions.assertFalse(server.isRunning())
        );
    }

    @Test
    void givenServerSocketCloseThrowsIOException_ThenShouldBeLogged() throws IOException {
        var serverSocket = Mockito.mock(ServerSocket.class);

        Mockito
                .doThrow(new IOException())
                .when(serverSocket)
                .close();

        var server = new Server(
                serverPort,
                data
        );
        server.setIsRunningStrategy(new NeverRunStrategy());
        server.start(serverSocket);

        List<TestLogRecord<?>> testLogRecords = lookupLogRecordsForClass(
                Server.class,
                TestLogLevel.ERROR
        );

        Assertions.assertEquals(
                1,
                testLogRecords.size()
        );

        TestLogRecord<?> logRecord = testLogRecords.getFirst();

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        "Failed to close server socket gracefully.",
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

    @Test
    void whenServerStartsAndThenStops_ThenShouldLogThatServerStartsAndStops() {
        var server = new Server(
                serverPort,
                data
        );
        server.setIsRunningStrategy(new NeverRunStrategy());
        server.start(null);

        List<TestLogRecord<?>> testLogRecords = lookupLogRecordsForClass(
                Server.class,
                TestLogLevel.INFO
        );

        Assertions.assertEquals(
                2,
                testLogRecords.size()
        );

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        "Starting Server",
                        testLogRecords
                                .getFirst()
                                .message()
                ),
                () -> Assertions.assertEquals(
                        TestLogLevel.INFO,
                        testLogRecords
                                .getFirst()
                                .logLevel()
                ),

                () -> Assertions.assertEquals(
                        "Stopping Server",
                        testLogRecords
                                .getLast()
                                .message()
                ),
                () -> Assertions.assertEquals(
                        TestLogLevel.INFO,
                        testLogRecords
                                .getLast()
                                .logLevel()
                )
        );
    }
}
