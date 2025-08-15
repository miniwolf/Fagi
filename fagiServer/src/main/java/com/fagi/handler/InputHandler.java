package com.fagi.handler;

import com.fagi.handler.request.RequestHandler;
import com.fagi.logging.FagiLogger;
import com.fagi.logging.FagiLoggerFactory;

import java.util.Map;
import java.util.Objects;

// TODO: zargess - javadoc
public record InputHandler(Map<Class<?>, RequestHandler<?>> handlers) {
    private static final FagiLogger LOGGER = FagiLoggerFactory.createLogger(InputHandler.class);

    public <T> void handleInput(T input) {
        if (input == null) {
            LOGGER.info(() -> "Input is null. Doing nothing.");
        } else if (handlers.containsKey(input.getClass())) {
            RequestHandler<T> requestHandler = lookupHandler(input);
            requestHandler.handleRequest(input);
        } else {
            LOGGER.info(() -> "Unknown handle: " + input.getClass());
        }
    }

    @SuppressWarnings("unchecked")
    private <T> RequestHandler<T> lookupHandler(T input) {
        Class<?> requestType = input.getClass();
        Objects.requireNonNull(requestType);
        RequestHandler<?> requestHandler = handlers.get(requestType);
        if (requestHandler == null) {
            throw new IllegalStateException("No handler registered for type: " + requestType);
        }
        if (!requestType.equals(requestHandler.getRequestClass())) {
            throw new IllegalStateException("There was a mismatch between the request type and the registered handler type. Request type was: " + requestType + ", the handler request type was: " + requestHandler.getRequestClass());
        }
        return (RequestHandler<T>) requestHandler;
    }
}
