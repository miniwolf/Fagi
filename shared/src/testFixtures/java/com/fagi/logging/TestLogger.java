package com.fagi.logging;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * <p>
 * This class is used to simulate a logger during unit tests, making it easier to test that the correct
 * logs were made.
 * </p>
 * <p>
 * All log records are stored in a list, ready to be read by tests.
 * </p>
 * <p>
 * Log records should be cleared after every unit test as loggers are stored statically,
 * making it possible for records to carry over to other tests.
 * </p>
 */
public class TestLogger<T> implements FagiLogger {
    private final List<TestLogRecord<?>> logRecords = new ArrayList<>();
    private final Class<T> loggerClass;

    public TestLogger(Class<T> tClass) {
        this.loggerClass = tClass;
    }

    @Override
    public void debug(Supplier<String> messageSupplier) {
        logRecords.add(new TestLogRecord<>(
                loggerClass,
                TestLogLevel.DEBUG,
                messageSupplier.get(),
                null
        ));
    }

    @Override
    public void debug(
            Throwable throwable,
            Supplier<String> messageSupplier) {
        logRecords.add(new TestLogRecord<>(
                loggerClass,
                TestLogLevel.DEBUG,
                messageSupplier.get(),
                throwable
        ));
    }

    @Override
    public void info(Supplier<String> messageSupplier) {
        logRecords.add(new TestLogRecord<>(
                loggerClass,
                TestLogLevel.INFO,
                messageSupplier.get(),
                null
        ));
    }

    @Override
    public void info(
            Throwable throwable,
            Supplier<String> messageSupplier) {
        logRecords.add(new TestLogRecord<>(
                loggerClass,
                TestLogLevel.INFO,
                messageSupplier.get(),
                throwable
        ));
    }

    @Override
    public void warning(Supplier<String> messageSupplier) {
        logRecords.add(new TestLogRecord<>(
                loggerClass,
                TestLogLevel.WARNING,
                messageSupplier.get(),
                null
        ));
    }

    @Override
    public void warning(
            Throwable throwable,
            Supplier<String> messageSupplier) {
        logRecords.add(new TestLogRecord<>(
                loggerClass,
                TestLogLevel.WARNING,
                messageSupplier.get(),
                throwable
        ));
    }

    @Override
    public void error(Supplier<String> messageSupplier) {
        logRecords.add(new TestLogRecord<>(
                loggerClass,
                TestLogLevel.ERROR,
                messageSupplier.get(),
                null
        ));
    }

    @Override
    public void error(
            Throwable throwable,
            Supplier<String> messageSupplier) {
        logRecords.add(new TestLogRecord<>(
                loggerClass,
                TestLogLevel.ERROR,
                messageSupplier.get(),
                throwable
        ));
    }

    public List<TestLogRecord<?>> getLogRecords() {
        return logRecords;
    }

    public Class<T> getLoggerClass() {
        return loggerClass;
    }

    /**
     * Clears the stored log records. Should be done after every unit test.
     */
    public void clearRecords() {
        logRecords.clear();
    }
}
