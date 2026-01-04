package com.fagi.http.server.util.match;

import com.fagi.http.HttpMethodType;
import com.fagi.http.server.model.RestMethodMatch;
import com.fagi.http.server.model.RestMethod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class helps match a specific HTTP request with an {@link RestMethodMatch}.
 * @author Marcus Haagh
 */
public final class RestMethodMatcher {

    private RestMethodMatcher() {
        throw new UnsupportedOperationException("Utility class - do not instantiate");
    }

    /**
     * <p>
     * Finds the first endpoint that has all its required parameters fulfilled by the given parameters. Sorts the list of endpoint matches before comparing them.
     * </p>
     * <p>
     * The endpoint with the most matching parameters will be chosen to get as specific method as possible.
     * </p>
     * <p>
     * Note: Having endpoints with the same path and parameter names can cause issues. This should be avoided.
     * </p>
     *
     * @param endpointCandidates the endpoints that matches the request path, but not necessarily the given parameters
     * @param queryParams        the query parameters given in the request
     * @param headers            the headers given in the request
     * @param requestHasBody     a flag indicating whether the request has a body or not
     * @return an endpoint match that matches all the parameters given in the request. Returns null if no match is found.
     */
    public static RestMethodMatch findMatching(
            List<RestMethodMatch> endpointCandidates,
            Map<String, String> queryParams,
            Map<String, List<String>> headers,
            boolean requestHasBody) {
        // We sort to ensure that we find the best candidate.
        Collections.sort(endpointCandidates);

        for (RestMethodMatch candidate : endpointCandidates) {
            RestMethod endpoint = candidate.restMethod();
            // Check query parameters
            boolean queryOk = endpoint
                    .requiredQueryParams()
                    .isEmpty() || queryParams
                    .keySet()
                    .containsAll(endpoint.requiredQueryParams());
            // Check headers
            boolean headersOk = endpoint
                    .requiredHeaderParams()
                    .isEmpty() || headers
                    .keySet()
                    .containsAll(endpoint.requiredHeaderParams());
            // Check body
            boolean bodyOk = !endpoint.isBodyRequired() || requestHasBody;
            if (queryOk && headersOk && bodyOk) {
                return candidate;
            }
        }
        return null;
    }


    /**
     * Find all candidate restMethods that matches the request path
     *
     * @param requestHttpMethod
     * @param requestPath       the path from the HTTP reqeust
     * @param restMethods         the possible restMethods to test
     * @return a list of restMethods that matches the request path
     */
    public static List<RestMethodMatch> findCandidates(
            HttpMethodType requestHttpMethod,
            String requestPath,
            List<RestMethod> restMethods) {
        var result = new ArrayList<RestMethodMatch>();

        for (RestMethod method : restMethods) {
            if (!method.httpMethod().equals(requestHttpMethod)) {
                continue;
            }
            Map<String, String> pathParams = matchRequestPathAgainstTemplate(
                    requestPath,
                    method.pathTemplate()
            );
            if (pathParams != null && pathParamsMatches(
                    pathParams,
                    method.pathParamTypes()
            )) {
                result.add(new RestMethodMatch(
                        method,
                        pathParams
                ));
            }
        }

        return result;
    }

    private static Map<String, String> matchRequestPathAgainstTemplate(
            String requestPath,
            String template) {
        String[] reqSegments = requestPath.split("/");
        String[] tempSegments = template.split("/");
        if (reqSegments.length != tempSegments.length) {
            return null;
        }

        Map<String, String> params = new HashMap<>();
        for (int i = 0; i < tempSegments.length; i++) {
            String tempSeg = tempSegments[i];
            String reqSeg = reqSegments[i];
            if (tempSeg.startsWith("{") && tempSeg.endsWith("}")) {
                String paramName = tempSeg.substring(
                        1,
                        tempSeg.length() - 1
                );
                params.put(
                        paramName,
                        reqSeg
                );
            } else if (!tempSeg.equals(reqSeg)) {
                return null;
            }
        }
        return params;
    }

    private static boolean pathParamsMatches(
            Map<String, String> params,
            Map<String, Class<?>> paramTypes) {
        for (var entry : params.entrySet()) {
            String paramName = entry.getKey();
            String paramValue = entry.getValue();
            Class<?> type = paramTypes.get(paramName);
            if (type == null || type == String.class) {
                continue;
            }

            if (!PrimitiveTypeMatcher.valueMatchesType(type, paramValue)) {
                return false;
            }
        }
        return true;
    }
}
