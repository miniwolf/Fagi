package com.fagi.http.server.handler;

import com.fagi.http.HttpCode;
import com.fagi.http.MimeType;

import java.io.IOException;

/**
 * A handler that encapsulates everything the {@link RestApiHandler} needs to create and send an HTTP response.
 *
 * @author Marcus Haagh
 */
public interface HttpResponseHandler {
    /**
     * Adds a header to the response.
     *
     * @param headerKey   the key of the header
     * @param headerValue the value of the header
     */
    void addHeader(
            String headerKey,
            String headerValue);

    /**
     * Sets the body of the response
     *
     * @param body the body of the response to be sent to the client
     */
    void setBody(String body);

    /**
     * Returns the body of the response
     *
     * @return the body of the response as a string
     */
    String getBody();

    /**
     * <p>Sends the response to the client.</p>
     * <p><b>OBS:</b> Framework does not support sending multiple responses.</p>
     *
     * @param httpCode the http code of the response
     * @param mimeType the mime type to be used in the Content-Type header
     * @throws IOException when sending response fails
     */
    void sendResponse(
            HttpCode httpCode,
            MimeType mimeType) throws IOException;
}
