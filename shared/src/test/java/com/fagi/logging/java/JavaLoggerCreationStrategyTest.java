package com.fagi.logging.java;

import com.fagi.logging.FagiLogger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class JavaLoggerCreationStrategyTest {
    @Test
    void verifyStrategyCreatesJavaLogger() {
        var strategy = new JavaLoggerCreationStrategy();

        FagiLogger logger = strategy.createLogger(JavaLoggerCreationStrategyTest.class);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(logger),
                () -> Assertions.assertInstanceOf(
                        JavaLogger.class,
                        logger
                )
        );
    }
}
