package com.fagi.http.server.handler;

import com.fagi.http.HttpMethodType;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Represents a framework-agnostic HTTP Request that provides everything the {@link RestApiHandler} needs from the request
 *
 * @author Marcus Haagh
 * @see RestApiHandler
 */
public interface HttpRequest {
    /**
     * The unique request id mostly used for logging purposes
     *
     * @return a unique UUID
     */
    UUID getRequestId();

    /**
     * The method of the HTTP Request
     *
     * @return an enum value representing the HTTP request method
     */
    HttpMethodType getMethod();

    /**
     * The path of the HTTP request
     *
     * @return a non-null string representing the path of the HTTP request
     */
    String getPath();

    /**
     * The query parameters given to the HTTP request
     *
     * @return a non-null map of query parameters
     */
    Map<String, String> getQueryParams();

    /**
     * The headers of the HTTP request
     *
     * @return a non-null map of headers given to the HTTP request
     */
    Map<String, List<String>> getHeaders();

    /**
     * Checks if HTTP request has a body
     *
     * @return true if request has a body, otherwise returns false
     */
    boolean hasBody() throws IOException;

    /**
     * The body of the HTTP request. If the body is null or an empty string, then this method should return {@link Optional#empty()}.
     * <br/>
     * This method should allow multiple calls.
     *
     * @return an optional value of the request body
     */
    String getBody();
}
