package com.fagi.http.server.exception;

/**
 * Exception thrown when parameter conversion fails.
 *
 * <p>
 * This exception is used to indicate that the server could not deserialize the given request body to the expected
 * data object type or convert the query/header/path parameter from a string to the expected type. It should only occur
 * when the given request input is invalid.
 * </p>
 * <p>
 * This is an indicator that the request was bad, and an {@link com.fagi.http.HttpCode#BAD_REQUEST} should be the result.
 * </p>
 *
 * @author Marcus Haagh
 */
public class RequestParameterConversionException extends Exception {
    public RequestParameterConversionException(
            Class<?> requestParameterType,
            Exception cause) {
        super(
                "Could not convert request parameter to type: " + requestParameterType,
                cause
        );
    }
}
