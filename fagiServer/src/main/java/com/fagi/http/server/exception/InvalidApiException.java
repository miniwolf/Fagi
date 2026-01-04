package com.fagi.http.server.exception;

import com.fagi.http.server.validation.RestEndpointValidationError;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Exception thrown when failing to create an HTTP handler.
 *
 * <p>
 * This exception is used to indicate that an api is not valid and forces the application to stop as there is a code error.
 * </p>
 * <p><strong>Runtime Exception:</strong> This is a runtime exception that only happens in cases where an api
 * interface is coded incorrectly, indicating a syntax error.</p>
 *
 * <p>
 * The server should anticipate this exception and should do nothing to handle it. The test suite should make
 * sure that it never happens.
 * </p>
 *
 * @author Marcus Haagh
 * @see com.fagi.http.server.factory.RestApiFactory
 * @see com.fagi.http.server.validation.RestEndpointValidationError
 * @see java.lang.RuntimeException
 */
public class InvalidApiException extends RuntimeException {
    /**
     * Create the exception with the api interface that wasn't valid and the errors found.
     *
     * @param apiInterface     the api interface that isn't valid
     * @param validationErrors a list of validation errors found when validating the api interface
     */
    public InvalidApiException(
            Class<?> apiInterface,
            List<RestEndpointValidationError> validationErrors) {
        super("The api interface " + apiInterface.getName() + " is not valid. The following errors were found: " + validationErrors);
    }

    /**
     * Create the exception with the api interface method that wasn't valid and the errors found.
     *
     * @param apiInterface     the api interface that isn't valid
     * @param method           the method in the api interface that isn't valid
     * @param validationErrors a list of validation errors found when validating the api interface
     */
    public InvalidApiException(
            Class<?> apiInterface,
            Method method,
            List<RestEndpointValidationError> validationErrors) {
        super("The api method " + apiInterface.getName() + "#" + method.getName() + "( " + createMethodParameterNameList(method) + ") is not valid. The following errors were found: " + validationErrors);
    }

    private static String createMethodParameterNameList(Method method) {
        return Arrays
                .stream(method.getParameterTypes())
                .map(Class::getSimpleName)
                .collect(java.util.stream.Collectors.joining(", "));
    }
}
