package com.fagi.handler;

import com.fagi.handler.request.RequestHandler;
import com.fagi.handler.request.auth.SessionRequestHandler;
import com.fagi.model.Session;
import com.fagi.model.UserNameAvailableRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

class InputHandlerTest {
    @Test
    void testShouldGetIllegalStateExceptionWhenRegistredClassMapsToNull() {
        var requestHandlers = new HashMap<Class<?>, RequestHandler<?>>();

        requestHandlers.put(
                UserNameAvailableRequest.class,
                null
        );

        var inputHandler = new InputHandler(requestHandlers);

        IllegalStateException exception = Assertions.assertThrows(
                IllegalStateException.class,
                () -> inputHandler.handleInput(new UserNameAvailableRequest("Charles"))
        );

        Assertions.assertEquals(
                "No handler registered for type: " + UserNameAvailableRequest.class,
                exception.getMessage()
        );
    }

    @Test
    void testShouldGetIllegalStateExceptionWhenRequestHandlerSupportedClassDoesNotMatchRegisteredClass() {
        var requestHandlers = new HashMap<Class<?>, RequestHandler<?>>();

        requestHandlers.put(
                UserNameAvailableRequest.class,
                new SessionRequestHandler(
                        null,
                        null,
                        null
                )
        );

        var inputHandler = new InputHandler(requestHandlers);

        IllegalStateException exception = Assertions.assertThrows(
                IllegalStateException.class,
                () -> inputHandler.handleInput(new UserNameAvailableRequest("Charles"))
        );

        Assertions.assertEquals(
                "There was a mismatch between the request type and the registered handler type. Request type was: " + UserNameAvailableRequest.class + ", the handler request type was: " + Session.class,
                exception.getMessage()
        );
    }
}