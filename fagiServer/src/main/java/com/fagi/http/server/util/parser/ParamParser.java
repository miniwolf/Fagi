package com.fagi.http.server.util.parser;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

// TODO: zargess - javadoc

/**
 * @author Marcus Haagh
 */
public final class ParamParser {
    private ParamParser() {
        throw new UnsupportedOperationException("Utility class - do not instantiate");
    }

    public static Map<String, String> parseQueryParams(String query) {
        if (query == null || query
                .trim()
                .isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, String> params = new HashMap<>();
        for (String pair : query.split("&")) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                params.put(
                        keyValue[0],
                        keyValue[1]
                );
            }
        }
        return params;
    }
}
