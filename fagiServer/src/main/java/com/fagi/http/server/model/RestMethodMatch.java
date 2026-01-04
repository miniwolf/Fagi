package com.fagi.http.server.model;

import java.util.Map;

// TODO: zargess - javadoc

/**
 *
 * @param restMethod
 * @param pathParams
 * @author Marcus Haagh
 */
public record RestMethodMatch(
        RestMethod restMethod,
        Map<String, String> pathParams) implements Comparable<RestMethodMatch> {
    @Override
    public int compareTo(RestMethodMatch o) {
        return restMethod.compareTo(o.restMethod);
    }
}
