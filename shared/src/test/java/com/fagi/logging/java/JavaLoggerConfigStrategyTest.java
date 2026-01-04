package com.fagi.logging.java;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Doesn't utilise {@link com.fagi.BaseFagiTest} as we want to test the real logging framework to check if the
 * logs work as expected.
 */
class JavaLoggerConfigStrategyTest {
    private static Path tempDir;
    private final Logger LOGGER = Logger.getLogger(JavaLoggerConfigStrategy.class.getName());
    private final TestJavaLoggingHandler testJavaLoggingHandler = new TestJavaLoggingHandler();
    private final JavaLoggerConfigStrategy strategy = new JavaLoggerConfigStrategy();

    @BeforeAll
    static void setupClass() throws IOException {
        tempDir = Path.of("build/test_logs");
        // Deleting the "build/test_logs" folder before executing the test.
        // This is done to mitigate Windows locking the log files in such a way that they cannot be
        // deleted after running the tests.
        if (Files.exists(tempDir)) {
            try (Stream<Path> walk = Files.walk(tempDir)) {
                walk
                        .sorted(Comparator.reverseOrder())
                        .forEach(path -> {
                            try {
                                Files.delete(path);
                            } catch (IOException e) {
                                throw new RuntimeException(
                                        "Failed to delete: " + path,
                                        e
                                );
                            }
                        });
            }
        }

        if (!Files.exists(tempDir)) {
            // Create the "build/test_logs" folder
            Files.createDirectory(tempDir);
        }
    }

    @BeforeEach
    void setup() {
        LOGGER.addHandler(testJavaLoggingHandler);
    }

    @Test
    void testRootLoggerHasConsoleAndFileHandlersAndBothHasCorrectFormatter() {
        strategy.setupDefaultConfiguration(createLogFile());

        var logger = Logger.getLogger("");

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        2,
                        logger.getHandlers().length
                ),
                () -> Assertions.assertTrue(Arrays
                                                    .stream(logger.getHandlers())
                                                    .anyMatch(h -> h instanceof ConsoleHandler)),
                () -> Assertions.assertTrue(Arrays
                                                    .stream(logger.getHandlers())
                                                    .anyMatch(h -> h instanceof FileHandler)),
                () -> Assertions.assertTrue(Arrays
                                                    .stream(logger.getHandlers())
                                                    .allMatch(h -> h.getFormatter() instanceof JavaLoggerFormatter))
        );

        cleanHandlers();
    }

    @Test
    void testConfigLogsIfFailsToMakeFileHandler() throws IOException {
        Path restrictedFile = createLogFile();
        Files.createFile(restrictedFile);

        // Remove read/write permissions on file to force IOException.
        restrictedFile
                .toFile()
                .setReadable(
                        false,
                        false
                );
        restrictedFile
                .toFile()
                .setWritable(
                        false,
                        false
                );

        strategy.setupDefaultConfiguration(restrictedFile.toAbsolutePath());

        List<LogRecord> logRecords = testJavaLoggingHandler.getLogRecords();
        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        1,
                        logRecords.size()
                ),
                () -> Assertions.assertEquals(
                        Level.SEVERE,
                        logRecords
                                .getFirst()
                                .getLevel()
                ),
                () -> Assertions.assertEquals(
                        "Failed to setup file handler for logger. Log statements will not be saved in files.",
                        logRecords
                                .getFirst()
                                .getMessage()
                ),
                () -> Assertions.assertInstanceOf(
                        IOException.class,
                        logRecords
                                .getFirst()
                                .getThrown()
                )
        );

        cleanHandlers();
    }

    @Test
    void testShouldNotLogFineRecords() {
        strategy.setupDefaultConfiguration(createLogFile());

        LOGGER.fine(() -> "This record should not be recorded.");

        Assertions.assertEquals(
                0,
                testJavaLoggingHandler
                        .getLogRecords()
                        .size()
        );

        cleanHandlers();
    }

    @Test
    void testShouldNotLogFinerRecords() {
        strategy.setupDefaultConfiguration(createLogFile());

        LOGGER.finer(() -> "This record should not be recorded.");

        Assertions.assertEquals(
                0,
                testJavaLoggingHandler
                        .getLogRecords()
                        .size()
        );

        cleanHandlers();
    }

    @Test
    void testShouldNotLogFinestRecords() {
        strategy.setupDefaultConfiguration(createLogFile());

        LOGGER.finest(() -> "This record should not be recorded.");

        Assertions.assertEquals(
                0,
                testJavaLoggingHandler
                        .getLogRecords()
                        .size()
        );

        cleanHandlers();
    }

    @Test
    void testShouldLogInfoRecords() {
        strategy.setupDefaultConfiguration(createLogFile());

        LOGGER.info(() -> "This record should be recorded.");

        Assertions.assertAll(() -> Assertions.assertEquals(
                1,
                testJavaLoggingHandler
                        .getLogRecords()
                        .size()
        ));

        cleanHandlers();
    }

    @Test
    void testShouldLogWarningRecords() {
        strategy.setupDefaultConfiguration(createLogFile());

        LOGGER.warning(() -> "This record should be recorded.");

        Assertions.assertAll(() -> Assertions.assertEquals(
                1,
                testJavaLoggingHandler
                        .getLogRecords()
                        .size()
        ));

        cleanHandlers();
    }

    @Test
    void testShouldLogSevereRecords() {
        strategy.setupDefaultConfiguration(createLogFile());

        LOGGER.severe(() -> "This record should be recorded.");

        Assertions.assertAll(() -> Assertions.assertEquals(
                1,
                testJavaLoggingHandler
                        .getLogRecords()
                        .size()
        ));

        cleanHandlers();
    }

    @Test
    void testCustomConfigCheckIsTrueWhenPropertyIsNonNull() {
        String sysPropertyOldValue = System.getProperty(JavaLoggerConfigStrategy.LOGGING_CONFIG_FILE_SYSTEM_PROPERTY_NAME);
        System.setProperty(
                JavaLoggerConfigStrategy.LOGGING_CONFIG_FILE_SYSTEM_PROPERTY_NAME,
                "Some non-null value"
        );

        boolean isCustomConfigAvailable = strategy.isCustomConfigurationAvailable();

        Assertions.assertTrue(isCustomConfigAvailable);

        if (sysPropertyOldValue != null) {
            System.setProperty(
                    JavaLoggerConfigStrategy.LOGGING_CONFIG_FILE_SYSTEM_PROPERTY_NAME,
                    sysPropertyOldValue
            );
        } else {
            System.clearProperty(JavaLoggerConfigStrategy.LOGGING_CONFIG_FILE_SYSTEM_PROPERTY_NAME);
        }

        cleanHandlers();
    }

    @Test
    void testCustomConfigCheckIsFalseWhenPropertyIsNull() {
        String sysPropertyOldValue = System.getProperty(JavaLoggerConfigStrategy.LOGGING_CONFIG_FILE_SYSTEM_PROPERTY_NAME);
        System.clearProperty(JavaLoggerConfigStrategy.LOGGING_CONFIG_FILE_SYSTEM_PROPERTY_NAME);

        boolean isCustomConfigAvailable = strategy.isCustomConfigurationAvailable();

        Assertions.assertFalse(isCustomConfigAvailable);

        if (sysPropertyOldValue != null) {
            System.setProperty(
                    JavaLoggerConfigStrategy.LOGGING_CONFIG_FILE_SYSTEM_PROPERTY_NAME,
                    sysPropertyOldValue
            );
        }

        cleanHandlers();
    }

    private static Path createLogFile() {
        return tempDir.resolve("test_" + UUID.randomUUID() + ".log");
    }

    private void cleanHandlers() {
        Handler[] handlers = LOGGER.getHandlers();
        for (Handler handler : handlers) {
            handler.flush();
            handler.close();
            LOGGER.removeHandler(handler);
        }
    }
}
