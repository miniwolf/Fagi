package com.fagi.http.server.handler.java;

import com.fagi.http.HttpCode;
import com.fagi.http.MimeType;
import com.fagi.http.server.handler.HttpResponseHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * An implementation of {@link HttpResponseHandler} using the Java standard library.
 *
 * @author Marcus Haagh
 * @see HttpResponseHandler
 */
public class JavaHttpResponseHandler implements HttpResponseHandler {
    private final HttpExchange httpExchange;
    private String body;

    public JavaHttpResponseHandler(HttpExchange httpExchange) {
        this.httpExchange = httpExchange;
    }

    @Override
    public void addHeader(
            String headerKey,
            String headerValue) {
        httpExchange
                .getResponseHeaders()
                .set(
                        headerKey,
                        headerValue
                );
    }

    @Override
    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String getBody() {
        return body;
    }

    @Override
    public void sendResponse(
            HttpCode httpCode,
            MimeType mimeType) throws IOException {
        if (body == null) {
            throw new IllegalArgumentException("Response body cannot be null.");
        }

        byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);

        httpExchange.sendResponseHeaders(
                httpCode.getCode(),
                bodyBytes.length
        );

        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(body.getBytes(StandardCharsets.UTF_8));
        }
        httpExchange.close();
    }
}
