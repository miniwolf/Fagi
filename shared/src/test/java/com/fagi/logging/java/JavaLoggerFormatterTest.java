package com.fagi.logging.java;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.LogRecord;

class JavaLoggerFormatterTest {
    private final JavaLoggerFormatter formatter = new JavaLoggerFormatter();

    @Test
    void testFormatterShouldGiveCorrectFormattedStrings() {
        var logRecord = new LogRecord(
                Level.WARNING,
                "This is the log message"
        );
        logRecord.setLoggerName(JavaLoggerFormatterTest.class.getName());
        logRecord.setThrown(new IOException());

        String dateTimeString = formatter.dateFormat.format(new Date(logRecord.getMillis()));

        var expectedFormattedLogEntry = dateTimeString + " [" + logRecord.getLevel() + "] " + logRecord.getLoggerName() + " - " + logRecord.getMessage() + System.lineSeparator() + IOException.class.getName();

        // Normalize line endings such that the test works on different operating systems
        String formattedLogRecord = formatter
                .format(logRecord)
                .replace(
                        "\n\r",
                        System.lineSeparator()
                );

        Assertions.assertTrue(formattedLogRecord.startsWith(expectedFormattedLogEntry));
    }
}