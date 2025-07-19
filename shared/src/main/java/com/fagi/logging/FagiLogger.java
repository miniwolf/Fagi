package com.fagi.logging;

import java.util.function.Supplier;

/**
 * <p>
 *     A Logger interface used to log messages for the Fagi server and client.
 * </p>
 * <p>
 *     This interface helps hide what logging framework is used, and thus makes it simpler to change framework without having to change a lot of code.
 * </p>
 *
 * @author Marcus Haagh
 */
public interface FagiLogger {
    /**
     * Logs message in DEBUG level
     *
     * @param messageSupplier a supplier that returns the message to be logged
     */
    void debug(Supplier<String> messageSupplier);

    /**
     * Logs message in DEBUG level along with the associated throwable
     *
     * @param throwable       the throwable to be logged
     * @param messageSupplier a supplier that returns the message to be logged
     */
    void debug(
            Throwable throwable,
            Supplier<String> messageSupplier);

    /**
     * Logs message in INFO level
     *
     * @param messageSupplier a supplier that returns the message to be logged
     */
    void info(Supplier<String> messageSupplier);

    /**
     * Logs message in INFO level along with the associated throwable
     *
     * @param throwable       the throwable to be logged
     * @param messageSupplier a supplier that returns the message to be logged
     */
    void info(
            Throwable throwable,
            Supplier<String> messageSupplier);

    /**
     * Logs message in WARNING level
     *
     * @param messageSupplier a supplier that returns the message to be logged
     */
    void warning(Supplier<String> messageSupplier);

    /**
     * Logs message in WARNING level along with the associated throwable
     *
     * @param throwable       the throwable to be logged
     * @param messageSupplier a supplier that returns the message to be logged
     */
    void warning(
            Throwable throwable,
            Supplier<String> messageSupplier);

    /**
     * Logs message in ERROR level
     *
     * @param messageSupplier a supplier that returns the message to be logged
     */
    void error(Supplier<String> messageSupplier);

    /**
     * Logs message in ERROR level along with the associated throwable
     *
     * @param throwable       the throwable to be logged
     * @param messageSupplier a supplier that returns the message to be logged
     */
    void error(
            Throwable throwable,
            Supplier<String> messageSupplier);
}
