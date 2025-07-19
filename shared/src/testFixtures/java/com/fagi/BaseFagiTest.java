package com.fagi;

import com.fagi.logging.FagiLoggerFactory;
import com.fagi.logging.TestLogLevel;
import com.fagi.logging.TestLogRecord;
import com.fagi.logging.TestLogger;
import com.fagi.logging.TestLoggerConfigurationStrategy;
import com.fagi.logging.TestLoggerCreationStrategy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * This abstract class offers utility to make unit tests easier to write.
 * </p>
 * <p>
 * All unit tests should inherit from this class.
 * </p>
 */
public abstract class BaseFagiTest {
    /**
     * All loggers registered during a test class will be stored here. This allows for reading log records
     * and clearing log records between tests.
     */
    private static final Map<Class<?>, TestLogger<?>> LOGGERS = new HashMap<>();

    @BeforeAll
    static void baseTestClassSetup() {
        FagiLoggerFactory.setLoggerCreationStrategy(new TestLoggerCreationStrategy(LOGGERS));
        FagiLoggerFactory.setLoggerConfigStrategy(new TestLoggerConfigurationStrategy(false));
    }

    /**
     * <p>
     * Performs tear down after every test.
     * </p>
     * <p>
     * This currently does the following:
     *     <ul>
     *         <li>Clears log records from all registered loggers.</li>
     *     </ul>
     * </p>
     */
    @AfterEach
    void baseTestTearDown() {
        for (TestLogger<?> logger : LOGGERS.values()) {
            logger.clearRecords();
        }
    }

    /**
     * <p>
     * Allows tests to lookup log records from a logger registered on the given Class.
     * </p>
     * <p>
     * To be used when testing if logs have been made correctly.
     * </p>
     *
     * @param tClass the class the logger was registered on.
     * @return a list of records made with a logger registered on the given Class.
     * @throws AssertionError if no logger has been registered on the given Class.
     */
    public List<TestLogRecord<?>> lookupLogRecordsForClass(Class<?> tClass) {
        var logger = LOGGERS.get(tClass);
        if (logger != null) {
            return logger.getLogRecords();
        }
        throw new AssertionError("No logger has been registered for class: " + tClass);
    }

    /**
     * <p>
     * Allows tests to lookup log records from a logger registered on the given Class with the given log level.
     * </p>
     * <p>
     * To be used when testing if logs have been made correctly.
     * </p>
     *
     * @param tClass   the class the logger was registered on.
     * @param logLevel the log level of records to be found.
     * @return a list of records made with a logger registered on the given Class at the given level.
     * @throws AssertionError if no logger has been registered on the given Class.
     */
    public List<TestLogRecord<?>> lookupLogRecordsForClass(
            Class<?> tClass,
            TestLogLevel logLevel) {
        return lookupLogRecordsForClass(tClass)
                .stream()
                .filter(logRecord -> logLevel.equals(logRecord.logLevel()))
                .toList();
    }
}
