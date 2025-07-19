package com.fagi.logging;

/**
 * A test representation of a log record. Made to be agnostic about the logging framework used by the production code.
 *
 * @param loggerClass the Class the logger that made the record was registered on.
 * @param logLevel    the log level the log record was made to.
 * @param message     the logged message.
 * @param throwable   the throwable associated with the message.
 */
public record TestLogRecord<T>(Class<T> loggerClass, TestLogLevel logLevel, String message, Throwable throwable) {
}
