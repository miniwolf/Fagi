package com.fagi.http.server.model;

import com.fagi.http.HttpMethodType;
import com.fagi.http.MimeType;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;

// TODO: zargess - javadoc

/**
 *
 * @param pathTemplate
 * @param method
 * @param pathParamTypes
 * @param requiredQueryParams
 * @param requiredHeaderParams
 * @param isBodyRequired
 * @param isPathStatic
 * @param creates
 * @param httpMethod
 * @param consumesMimeType
 * @param producesMimeType
 * @author Marcus Haagh
 */
public record RestMethod(
        String pathTemplate,
        Method method,
        Map<String, Class<?>> pathParamTypes,
        Set<String> requiredQueryParams,
        Set<String> requiredHeaderParams,
        boolean isBodyRequired,
        boolean isPathStatic,
        boolean creates,
        HttpMethodType httpMethod,
        MimeType consumesMimeType,
        MimeType producesMimeType) implements Comparable<RestMethod> {

    /**
     * <p>
     * This comparison ensure that we sort in descending order. The order helps ensure that the most specific {@link RestMethod}
     * is chosen when trying to match endpoints to requests.
     * </p>
     * <p>
     * The priority of comparison is this:
     *     <ul>
     *         <li>Is the path static.</li>
     *         <li>Number of required query parameters.</li>
     *         <li>Number of required header parameters.</li>
     *         <li>Is a body required.</li>
     *         <li>The type of the path params as tie-breaker.</li>
     *     </ul>
     * </p>
     *
     * @param o object to be compared to this on
     * @return a negative number if more specific, a positive number if less specific and 0 if equally specific
     */
    @Override
    public int compareTo(RestMethod o) {
        return Comparator
                .comparing(
                        RestMethod::isPathStatic,
                        Comparator.reverseOrder()
                )
                .thenComparing(
                        endpoint -> endpoint.requiredQueryParams.size(),
                        Comparator.reverseOrder()
                )
                .thenComparing(
                        endpoint -> endpoint.requiredHeaderParams.size(),
                        Comparator.reverseOrder()
                )
                .thenComparing(
                        RestMethod::isBodyRequired,
                        Comparator.reverseOrder()
                )
                .thenComparing(
                        RestMethod::calculatePathParamSpecificity,
                        Comparator.reverseOrder()
                )
                .compare(
                        this,
                        o
                );
    }

    /**
     * <p>Calculates how specific we can determine a path param by checking the types of the path params.</p>
     * <p>
     * The calculation works as follows:
     *     <ul>
     *         <li>No path params -> 0</li>
     *         <li>All path params are of type string -> 1</li>
     *         <li>At least one path param of type different from string -> 2</li>
     *     </ul>
     * </p>
     *
     * @return an integer value denoting how specific the path params can be determined
     */
    private int calculatePathParamSpecificity() {
        if (pathParamTypes.isEmpty()) {
            return 0;
        }

        return pathParamTypes
                .values()
                .stream()
                .allMatch(String.class::equals) ? 1 : 2;
    }
}
