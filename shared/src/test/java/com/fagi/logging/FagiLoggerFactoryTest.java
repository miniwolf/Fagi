package com.fagi.logging;

import com.fagi.BaseFagiTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

public class FagiLoggerFactoryTest extends BaseFagiTest {
    @Test
    void testCanCreateLogger() {
        FagiLogger logger = FagiLoggerFactory.createLogger(FagiLoggerFactoryTest.class);

        Assertions.assertNotNull(logger);
        Assertions.assertInstanceOf(
                TestLogger.class,
                logger
        );
        Assertions.assertEquals(
                FagiLoggerFactoryTest.class,
                ((TestLogger) logger).getLoggerClass()
        );
    }

    @Test
    void testConfigurationAvailableWhenStrategyReturnsTrue() {
        FagiLoggerFactory.setLoggerConfigStrategy(new TestLoggerConfigurationStrategy(true));

        Assertions.assertTrue(FagiLoggerFactory.isCustomConfigurationAvailable());
    }

    @Test
    void testConfigurationNotAvailableWhenStrategyReturnsFalse() {
        FagiLoggerFactory.setLoggerConfigStrategy(new TestLoggerConfigurationStrategy(false));

        Assertions.assertFalse(FagiLoggerFactory.isCustomConfigurationAvailable());
    }

    @Test
    void testFactoryUsesConfigurationStrategyWhenNoCustomConfigurationIsProvided() {
        // Creates logger to be used by the factory. This allows us to check the log records later.
        FagiLoggerFactory.setLogger(FagiLoggerFactory.createLogger(FagiLoggerFactory.class));

        TestLoggerConfigurationStrategy loggerConfigStrategy = new TestLoggerConfigurationStrategy(false);
        FagiLoggerFactory.setLoggerConfigStrategy(loggerConfigStrategy);

        Path logFile = Path.of("/some/path");
        FagiLoggerFactory.setupDefaultConfiguration(logFile);

        List<TestLogRecord<?>> testLogRecords = lookupLogRecordsForClass(FagiLoggerFactory.class);

        Assertions.assertAll(
                () -> Assertions.assertTrue(testLogRecords.isEmpty()),
                () -> Assertions.assertTrue(loggerConfigStrategy.isConfigured()),
                () -> Assertions.assertEquals(
                        logFile,
                        loggerConfigStrategy.getLogFile()
                )
        );
    }

    @Test
    void testFactoryDebugLogsWhenItShouldntUseDefaultConfiguration() {
        // Creates logger to be used by the factory. This allows us to check the log records later.
        FagiLoggerFactory.setLogger(FagiLoggerFactory.createLogger(FagiLoggerFactory.class));

        TestLoggerConfigurationStrategy loggerConfigStrategy = new TestLoggerConfigurationStrategy(true);
        FagiLoggerFactory.setLoggerConfigStrategy(loggerConfigStrategy);

        FagiLoggerFactory.setupDefaultConfiguration(Path.of("/some/path"));

        List<TestLogRecord<?>> testLogRecords = lookupLogRecordsForClass(FagiLoggerFactory.class);

        Assertions.assertEquals(
                1,
                testLogRecords.size()
        );

        TestLogRecord<?> logRecord = testLogRecords.getFirst();

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        TestLogLevel.DEBUG,
                        logRecord.logLevel()
                ),
                () -> Assertions.assertEquals(
                        FagiLoggerFactory.class,
                        logRecord.loggerClass()
                ),
                () -> Assertions.assertEquals(
                        "Custom configuration has been provided. No further configuration is made.",
                        logRecord.message()
                ),
                () -> Assertions.assertNull(logRecord.throwable()),
                () -> Assertions.assertFalse(loggerConfigStrategy.isConfigured())
        );
    }
}
