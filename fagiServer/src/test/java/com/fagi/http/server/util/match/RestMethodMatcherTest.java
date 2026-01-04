package com.fagi.http.server.util.match;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

class RestMethodMatcherTest {

    @Test
    void testUnsupportedOperationExceptionThrownWhenNewingClass() {
        var constructors = RestMethodMatcher.class.getDeclaredConstructors();

        Assertions.assertEquals(
                1,
                constructors.length
        );

        var constructor = constructors[0];

        constructor.setAccessible(true);

        InvocationTargetException invocationTargetException = Assertions.assertThrows(
                InvocationTargetException.class,
                constructor::newInstance
        );

        Assertions.assertInstanceOf(
                UnsupportedOperationException.class,
                invocationTargetException.getCause()
        );

        Assertions.assertEquals(
              "Utility class - do not instantiate",
                invocationTargetException
                        .getCause()
                        .getMessage()
        );
    }
}