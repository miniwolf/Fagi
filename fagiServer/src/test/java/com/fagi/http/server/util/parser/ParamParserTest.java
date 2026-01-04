package com.fagi.http.server.util.parser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Map;

class ParamParserTest {
    @Test
    void testUnsupportedOperationExceptionThrownWhenNewingClass() {
        var constructors = ParamParser.class.getDeclaredConstructors();

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

    @Test
    void testNullGivesEmptyMap() {
        Assertions.assertEquals(
                Collections.emptyMap(),
                ParamParser.parseQueryParams(null)
        );
    }

    @Test
    void testEmptyStringGivesEmptyMap() {
        Assertions.assertEquals(
                Collections.emptyMap(),
                ParamParser.parseQueryParams("")
        );
    }

    @Test
    void testStringWithNoQueryParamGivesEmptyMap() {
        Assertions.assertEquals(
                Collections.emptyMap(),
                ParamParser.parseQueryParams("hejmed&dig=")
        );
    }

    @Test
    void testStringWithQueryParamGivesMapWithThatParam() {
        Assertions.assertEquals(Map.of("navn", "jens"),
                                ParamParser.parseQueryParams("navn=jens")
        );
    }
}