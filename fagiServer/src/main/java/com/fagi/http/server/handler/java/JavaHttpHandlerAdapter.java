package com.fagi.http.server.handler.java;

import com.fagi.http.server.handler.HttpRequest;
import com.fagi.http.server.handler.HttpResponseHandler;
import com.fagi.http.server.handler.RestApiHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

/**
 *
 * @param <API>
 * @param <ENDPOINT>
 * @author Marcus Haagh
 */
public class JavaHttpHandlerAdapter<API, ENDPOINT extends API> implements HttpHandler {

    private RestApiHandler<API, ENDPOINT> endpointHandler;

    public JavaHttpHandlerAdapter(RestApiHandler<API, ENDPOINT> endpointHandler) {
        this.endpointHandler = endpointHandler;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        HttpRequest request = new JavaHttpRequest(exchange);
        HttpResponseHandler responseHandler = new JavaHttpResponseHandler(exchange);
        endpointHandler.handle(
                request,
                responseHandler
        );
    }
}
