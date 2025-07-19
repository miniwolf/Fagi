package com.fagi.logging.java;

import com.fagi.logging.FagiLogger;

import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A {@link FagiLogger} implementation that uses {@link Logger} from the Java Standard Library.
 *
 * @author Marcus Haagh
 * @see FagiLogger
 * @see Logger
 */
public class JavaLogger implements FagiLogger {
    private final Logger logger;

    public <T> JavaLogger(Class<T> tClass) {
        this.logger = Logger.getLogger(tClass.getName());
    }

    @Override
    public void debug(Supplier<String> messageSupplier) {
        logger.log(
                Level.FINE,
                messageSupplier
        );
    }

    @Override
    public void debug(
            Throwable throwable,
            Supplier<String> messageSupplier) {
        logger.log(
                Level.FINE,
                throwable,
                messageSupplier
        );
    }

    @Override
    public void info(Supplier<String> messageSupplier) {
        logger.log(
                Level.INFO,
                messageSupplier
        );
    }

    @Override
    public void info(
            Throwable throwable,
            Supplier<String> messageSupplier) {
        logger.log(
                Level.INFO,
                throwable,
                messageSupplier
        );
    }

    @Override
    public void warning(Supplier<String> messageSupplier) {
        logger.log(
                Level.WARNING,
                messageSupplier
        );
    }

    @Override
    public void warning(
            Throwable throwable,
            Supplier<String> messageSupplier) {
        logger.log(
                Level.WARNING,
                throwable,
                messageSupplier
        );
    }

    @Override
    public void error(Supplier<String> messageSupplier) {
        logger.log(
                Level.SEVERE,
                messageSupplier
        );
    }

    @Override
    public void error(
            Throwable throwable,
            Supplier<String> messageSupplier) {
        logger.log(
                Level.SEVERE,
                throwable,
                messageSupplier
        );
    }
}
