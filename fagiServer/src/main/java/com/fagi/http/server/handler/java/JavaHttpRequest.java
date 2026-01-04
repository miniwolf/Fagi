package com.fagi.http.server.handler.java;

import com.fagi.http.HttpMethodType;
import com.fagi.http.server.handler.HttpRequest;
import com.fagi.http.server.util.parser.ParamParser;
import com.fagi.logging.FagiLogger;
import com.fagi.logging.FagiLoggerFactory;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * An implementation of {@link HttpRequest} using the Java standard library.
 *
 * @author Marcus Haagh
 * @see HttpRequest
 */
public class JavaHttpRequest implements HttpRequest {
    private static final FagiLogger LOGGER = FagiLoggerFactory.createLogger(JavaHttpRequest.class);
    private final HttpExchange httpExchange;
    private final UUID requestId;
    private String cachedRequestBody;

    public JavaHttpRequest(HttpExchange httpExchange) {
        this.httpExchange = httpExchange;
        requestId = UUID.randomUUID();
    }

    @Override
    public UUID getRequestId() {
        return requestId;
    }

    @Override
    public HttpMethodType getMethod() {
        return HttpMethodType.valueOf(httpExchange.getRequestMethod());
    }

    @Override
    public String getPath() {
        return httpExchange
                .getRequestURI()
                .getPath();
    }

    @Override
    public Map<String, String> getQueryParams() {
        return ParamParser.parseQueryParams(httpExchange
                                                    .getRequestURI()
                                                    .getQuery());
    }

    @Override
    public Map<String, List<String>> getHeaders() {
        return httpExchange.getRequestHeaders();
    }

    @Override
    public boolean hasBody() throws IOException {
        cacheRequestBody();

        return cachedRequestBody != null && !cachedRequestBody
                .trim()
                .isEmpty();
    }

    @Override
    public String getBody() {
        try {
            cacheRequestBody();

            return cachedRequestBody;
        } catch (IOException e) {
            LOGGER.error(
                    e,
                    () -> "Failed to read the body from request."
            );
            return null;
        }
    }

    private void cacheRequestBody() throws IOException {
        if (cachedRequestBody == null) {
            var body = httpExchange
                    .getRequestBody()
                    .readAllBytes();
            cachedRequestBody = new String(body);
        }
    }
}
