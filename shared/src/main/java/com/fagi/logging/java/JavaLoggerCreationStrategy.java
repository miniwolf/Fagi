package com.fagi.logging.java;

import com.fagi.logging.FagiLogger;
import com.fagi.logging.FagiLoggerCreationStrategy;

/**
 * Creates a {@link java.util.logging.Logger} from the Java Standard Library
 *
 * @author Marcus Haagh
 */
public class JavaLoggerCreationStrategy implements FagiLoggerCreationStrategy {
    @Override
    public <T> FagiLogger createLogger(Class<T> tClass) {
        return new JavaLogger(tClass);
    }
}
