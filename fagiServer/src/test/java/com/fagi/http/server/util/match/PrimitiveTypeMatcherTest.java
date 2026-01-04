package com.fagi.http.server.util.match;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

class PrimitiveTypeMatcherTest {
    @Test
    void testUnsupportedOperationExceptionThrownWhenNewingClass() {
        var constructors = PrimitiveTypeMatcher.class.getDeclaredConstructors();

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
    void testIntMatches() {
        Assertions.assertAll(
                () -> Assertions.assertTrue(PrimitiveTypeMatcher.valueMatchesType(
                        int.class,
                        "3"
                )),
                () -> Assertions.assertTrue(PrimitiveTypeMatcher.valueMatchesType(
                        Integer.class,
                        "3"
                )),
                () -> Assertions.assertFalse(PrimitiveTypeMatcher.valueMatchesType(
                        int.class,
                        "not an integer"
                ))
        );
    }

    @Test
    void testLongMatches() {
        Assertions.assertAll(
                () -> Assertions.assertTrue(PrimitiveTypeMatcher.valueMatchesType(
                        long.class,
                        "3000000000"
                )),
                () -> Assertions.assertTrue(PrimitiveTypeMatcher.valueMatchesType(
                        Long.class,
                        "3000000000"
                )),
                () -> Assertions.assertFalse(PrimitiveTypeMatcher.valueMatchesType(
                        long.class,
                        "not long"
                ))
        );
    }

    @Test
    void testBooleanMatches() {
        Assertions.assertAll(
                () -> Assertions.assertTrue(PrimitiveTypeMatcher.valueMatchesType(
                        boolean.class,
                        "tRue"
                )),
                () -> Assertions.assertTrue(PrimitiveTypeMatcher.valueMatchesType(
                        boolean.class,
                        "FalSE"
                )),
                () -> Assertions.assertTrue(PrimitiveTypeMatcher.valueMatchesType(
                        Boolean.class,
                        "tRuE"
                )),
                () -> Assertions.assertTrue(PrimitiveTypeMatcher.valueMatchesType(
                        Boolean.class,
                        "falSE"
                )),
                () -> Assertions.assertFalse(PrimitiveTypeMatcher.valueMatchesType(
                        boolean.class,
                        "fisk"
                )),
                () -> Assertions.assertFalse(PrimitiveTypeMatcher.valueMatchesType(
                        Boolean.class,
                        "3"
                ))
        );
    }

    @Test
    void testDoubleMatches() {
        Assertions.assertAll(
                () -> Assertions.assertTrue(PrimitiveTypeMatcher.valueMatchesType(
                        double.class,
                        "3.5"
                )),
                () -> Assertions.assertTrue(PrimitiveTypeMatcher.valueMatchesType(
                        Double.class,
                        "4.2"
                )),
                () -> Assertions.assertFalse(PrimitiveTypeMatcher.valueMatchesType(
                        double.class,
                        "true"
                ))
        );
    }

    @Test
    void testFloatMatches() {
        Assertions.assertAll(
                () -> Assertions.assertTrue(PrimitiveTypeMatcher.valueMatchesType(
                        float.class,
                        "3.5"
                )),
                () -> Assertions.assertTrue(PrimitiveTypeMatcher.valueMatchesType(
                        Float.class,
                        "4.2"
                )),
                () -> Assertions.assertFalse(PrimitiveTypeMatcher.valueMatchesType(
                        float.class,
                        "true"
                ))
        );
    }

    @Test
    void testShortMatches() {
        Assertions.assertAll(
                () -> Assertions.assertTrue(PrimitiveTypeMatcher.valueMatchesType(
                        short.class,
                        "42"
                )),
                () -> Assertions.assertTrue(PrimitiveTypeMatcher.valueMatchesType(
                        Short.class,
                        "42"
                )),
                () -> Assertions.assertFalse(PrimitiveTypeMatcher.valueMatchesType(
                        short.class,
                        "32768"
                ))
        );
    }

    @Test
    void testByteMatches() {
        Assertions.assertAll(
                () -> Assertions.assertTrue(PrimitiveTypeMatcher.valueMatchesType(
                        byte.class,
                        "42"
                )),
                () -> Assertions.assertTrue(PrimitiveTypeMatcher.valueMatchesType(
                        Byte.class,
                        "42"
                )),
                () -> Assertions.assertFalse(PrimitiveTypeMatcher.valueMatchesType(
                        byte.class,
                        "130"
                ))
        );
    }

    @Test
    void testCharMatches() {
        Assertions.assertAll(
                () -> Assertions.assertTrue(PrimitiveTypeMatcher.valueMatchesType(
                        char.class,
                        "a"
                )),
                () -> Assertions.assertTrue(PrimitiveTypeMatcher.valueMatchesType(
                        Character.class,
                        "q"
                )),
                () -> Assertions.assertFalse(PrimitiveTypeMatcher.valueMatchesType(
                        char.class,
                        "hej"
                ))
        );
    }

    @Test
    void testComplexTypeDoesNotMatch() {
        Assertions.assertFalse(
                PrimitiveTypeMatcher.valueMatchesType(List.class, "[2]")
        );
    }
}