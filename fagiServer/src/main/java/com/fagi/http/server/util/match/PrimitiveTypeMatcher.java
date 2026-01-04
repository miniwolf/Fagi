package com.fagi.http.server.util.match;

// TODO: zargess - Javadoc

/**
 *
 * @author Marcus Haagh
 */
public class PrimitiveTypeMatcher {
    private PrimitiveTypeMatcher() {
        throw new UnsupportedOperationException("Utility class - do not instantiate");
    }

    // TODO: zargess - Javadoc
    public static boolean valueMatchesType(Class<?> type, String value) {
        try {
            if (type == int.class || type == Integer.class) {
                Integer.parseInt(value);
            } else if (type == long.class || type == Long.class) {
                Long.parseLong(value);
            } else if (type == boolean.class || type == Boolean.class) {
                if (!value.equalsIgnoreCase("true") && !value.equalsIgnoreCase("false")) {
                    return false;
                }
            } else if (type == double.class || type == Double.class) {
                Double.parseDouble(value);
            } else if (type == float.class || type == Float.class) {
                Float.parseFloat(value);
            } else if (type == short.class || type == Short.class) {
                Short.parseShort(value);
            } else if (type == byte.class || type == Byte.class) {
                Byte.parseByte(value);
            } else if (type == char.class || type == Character.class) {
                if (value.length() != 1) {
                    return false;
                }
            } else {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
