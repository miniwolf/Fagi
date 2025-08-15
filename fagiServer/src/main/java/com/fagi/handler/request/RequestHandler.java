package com.fagi.handler.request;

public interface RequestHandler<T> {
    Class<T> getRequestClass();

    void handleRequest(T request);
}
